package com.hive.exts


import com.hive.ApiErrorException
import com.hive.DataState
import com.hive.model.DataModel
import com.hive.model.Error
import com.hive.model.ErrorModel
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.test.assertIs

class DataStateExtensionsTest {

    
    private fun createErrorModel(description: String, error: String): ErrorModel {
        return ErrorModel(error =  Error(description, error, 400, "Bad Request", 400))
    }

    
    private fun createApiErrorException(description: String = "API Error", error: String = "invalid_request"): ApiErrorException {
        return ApiErrorException(createErrorModel(description, error))
    }

    
    @Test
    fun filterloading_should_emit_loading_state() = runTest {
        val flow = flow {
            emit(DataState.Loading(1))
            emit(DataState.Success("data", 1))
        }

        val result = flow.filterLoading().first()

        assertIs<DataState.Loading>(result)
    }

    
    @Test
    fun filtersuccess_should_emit_success_state() = runTest {
        val flow = flow {
            emit(DataState.Loading( 1))
            emit(DataState.Success("data", 1))
        }

        val result = flow.filterSuccess().first()

        assertTrue(result.data == "data")
    }

    
    @Test
    fun filtererror_should_emit_error_state() = runTest {
        val exception = RuntimeException("Error")
        val flow = flow {
            emit(DataState.Loading( 1))
            emit(DataState.Error(exception, 1))
        }

        val result = flow.filterError().first()

        assertTrue(result.throwable == exception)
    }

    
    @Test
    fun filterapierror_should_emit_apierror_state() = runTest {
        val apiErrorException = createApiErrorException("API Error", "invalid_request")
        val flow = flow {
            emit(DataState.Loading( 1))
            emit(DataState.ApiError(apiErrorException, 1))
        }

        val result = flow.filterApiError().first()

        assertTrue(result.apiErrorException == apiErrorException)
    }

    
    @Test
    fun filteranyerror_should_emit_error_state_for_both_error_and_apierror() = runTest {
        val exception = RuntimeException("Error")
        val apiErrorException = createApiErrorException("API Error", "invalid_request")
        val flow = flow {
            emit(DataState.Error(exception, 1))
            emit(DataState.ApiError(apiErrorException, 2))
        }

        val results = flow.filterAnyError().toList()

        assertEquals(2, results.size)
        assertTrue(results[0].throwable == exception)
        assertTrue(results[1].throwable == apiErrorException)
    }

    
    @Test
    fun extractsuccessdata_should_emit_data_from_success_state() = runTest {
        val flow = flow {
            emit(DataState.Success("data", 1))
        }

        val result = flow.extractSuccessData().first()

        assertEquals("data", result)
    }

    
    @Test
    fun extractdatamodel_should_emit_datamodel_from_success_state() = runTest {
        val dataModel = DataModel("data")
        val flow = flow {
            emit(DataState.Success(dataModel, 1))
        }

        val result = flow.extractDataModel().first()

        assertEquals(dataModel, result)
    }

    
    @Test
    fun extractdatafromdatamodel_should_emit_data_from_datamodel() = runTest {
        val dataModel = DataModel("data")
        val flow = flow {
            emit(DataState.Success(dataModel, 1))
        }

        val result = flow.extractDataFromDataModel().first()

        assertEquals("data", result)
    }

    
    @Test
    fun requireextractdatafromdatamodel_should_emit_non_null_data_from_datamodel() = runTest {
        val dataModel = DataModel("data")
        val flow = flow {
            emit(DataState.Success(dataModel, 1))
        }

        val result = flow.requireExtractDataFromDataModel().first()

        assertEquals("data", result)
    }

    
    @Test
    fun onerror_should_execute_callback_for_error_state() = runTest {
        val exception = RuntimeException("Error")
        val flow = flow {
            emit(DataState.Error(exception, 1))
        }
        var errorMessage: String? = null

        flow.onError { message ->
            errorMessage = message
        }.collect()

        assertEquals("Error", errorMessage)
    }

    
    @Test
    fun onerror_should_execute_callback_for_apierror_state() = runTest {
        val apiErrorException = createApiErrorException("API Error", "invalid_request")
        val flow = flow {
            emit(DataState.ApiError(apiErrorException, 1))
        }
        var errorMessage: String? = null

        flow.onError { message ->
            errorMessage = message
        }.collect()

        assertEquals("API Error", errorMessage)
    }

    
    @Test
    fun onanyerrorstate_should_execute_callback_for_any_error_state() = runTest {
        val exception = RuntimeException("Error")
        val apiErrorException = createApiErrorException("API Error", "invalid_request")
        val flow = flow {
            emit(DataState.Error(exception, 1))
            emit(DataState.ApiError(apiErrorException, 2))
        }
        val errorStates = mutableListOf<DataState.Error>()

        flow.onAnyErrorState { errorState ->
            errorStates.add(errorState)
        }.collect()

        assertEquals(2, errorStates.size)
        assertEquals(exception, errorStates[0].throwable)
        assertEquals(apiErrorException, errorStates[1].throwable)
    }

    
    @Test
    fun datastateflow_should_emit_success_state_for_successful_block_execution() = runTest {
        val requestId = 1
        val flow = dataStateFlow(requestId) {
            "data"
        }
        val result = flow.filterSuccess().first()

        assertTrue(result.data?.data == "data")
    }

    
    @Test
    fun datastateflow_should_emit_error_state_for_failed_block_execution() = runTest {
        val requestId = 1
        val exception = RuntimeException("Error")
        val flow = dataStateFlow<String>(requestId) {
            throw exception
        }

        val result = flow.filterError().first()

        assertTrue(result.throwable == exception)
    }

    
    @Test
    fun datastateflow_should_emit_apierror_state_for_apierrorexception() = runTest {
        val requestId = 1
        val apiErrorException = createApiErrorException("API Error", "invalid_request")
        val flow = dataStateFlow<String>(requestId) {
            throw apiErrorException
        }
        val result = flow.filterApiError().first()

        assertTrue(result.apiErrorException == apiErrorException)
    }

    
    @Test
    fun filterloading_should_not_emit_if_no_loading_state_exists() = runTest {
        val flow = flow {
            emit(DataState.Success("data", 1))
        }

        val result = flow.filterLoading().toList()

        assertTrue(result.isEmpty())
    }

    
    @Test
    fun filtersuccess_should_not_emit_if_no_success_state_exists() = runTest {
        val flow = flow {
            emit(DataState.Loading( 1))
        }

        val result = flow.filterSuccess().toList()

        assertTrue(result.isEmpty())
    }

    
    @Test
    fun extractsuccessdata_should_emit_null_for_success_state_with_null_data() = runTest {
        val flow = flow {
            emit(DataState.Success(null, 1))
        }

        val result = flow.extractSuccessData().first()

        assertNull(result)
    }

    
    @Test(expected = IllegalStateException::class)
    fun requireextractdatafromdatamodel_should_throw_if_datamodel_data_is_null() = runTest {
        val dataModel = DataModel<String?>(null)
        val flow = flow {
            emit(DataState.Success(dataModel, 1))
        }

        flow.requireExtractDataFromDataModel().collect()
    }

    
    @Test
    fun onerrorstate_should_execute_callback_for_datastate_error() = runTest {
        
        val exception = RuntimeException("Error")
        val flow = flow {
            emit(DataState.Error(exception, 1))
        }
        var callbackInvoked = false

        
        flow.onErrorState { errorState ->
            callbackInvoked = true
            assertEquals(exception, errorState.throwable)
            assertEquals(1, errorState.requestId)
        }.collect()

        
        assertTrue(callbackInvoked)
    }

    
    @Test
    fun onerrorstate_should_not_execute_callback_for_non_error_states() = runTest {
        
        val flow = flow {
            emit(DataState.Success("data", 1))
            emit(DataState.Loading( 1))
            emit(DataState.ApiError(createApiErrorException(), 1))
        }
        var callbackInvoked = false

        
        flow.onErrorState {
            callbackInvoked = true
        }.collect()

        
        assertFalse(callbackInvoked)
    }

    
    @Test
    fun onapierrorstate_should_execute_callback_for_datastate_apierror() = runTest {
        
        val apiErrorException = createApiErrorException()
        val flow = flow {
            emit(DataState.ApiError(apiErrorException, 1))
        }
        var callbackInvoked = false

        
        flow.onApiErrorState { apiErrorState ->
            callbackInvoked = true
            assertEquals(apiErrorException, apiErrorState.apiErrorException)
            assertEquals(1, apiErrorState.requestId)
        }.collect()

        
        assertTrue(callbackInvoked)
    }

    
    @Test
    fun onapierrorstate_should_not_execute_callback_for_non_apierror_states() = runTest {
        
        val flow = flow {
            emit(DataState.Success("data", 1))
            emit(DataState.Loading( 1))
            emit(DataState.Error(RuntimeException("Error"), 1))
        }
        var callbackInvoked = false

        
        flow.onApiErrorState {
            callbackInvoked = true
        }.collect()

        
        assertFalse(callbackInvoked)
    }

    
    @Test
    fun onerrorstate_and_onapierrorstate_should_handle_respective_error_states() = runTest {
        
        val exception = RuntimeException("Error")
        val apiErrorException = createApiErrorException()
        val flow = flow {
            emit(DataState.Error(exception, 1))
            emit(DataState.ApiError(apiErrorException, 2))
        }
        var errorCallbackInvoked = false
        var apiErrorCallbackInvoked = false

        
        flow.onErrorState { errorState ->
            errorCallbackInvoked = true
            assertEquals(exception, errorState.throwable)
            assertEquals(1, errorState.requestId)
        }.onApiErrorState { apiErrorState ->
            apiErrorCallbackInvoked = true
            assertEquals(apiErrorException, apiErrorState.apiErrorException)
            assertEquals(2, apiErrorState.requestId)
        }.collect()

        
        assertTrue(errorCallbackInvoked)
        assertTrue(apiErrorCallbackInvoked)
    }
    
    @Test
    fun mapstate_should_transform_datastate_success_data() = runTest {
        
        val flow = flow {
            emit(DataState.Success("data", 1))
        }

        
        val result = flow.mapState { it?.length }.first()

        
        assertTrue(result is DataState.Success && result.data == 4)
    }

    @Test
    fun mapstate_should_not_transform_error__apierror__or_loading_states() = runTest {
        
        val exception = RuntimeException("Error")
        val apiErrorException = createApiErrorException()
        val flow = flow {
            emit(DataState.Error(exception, 1))
            emit(DataState.ApiError(apiErrorException, 2))
            emit(DataState.Loading( 3))
        }

        
        val results = flow.mapState { it }.toList()

        
        assertEquals(3, results.size)
        assertTrue(results[0] is DataState.Error && (results[0] as DataState.Error).throwable == exception)
        assertTrue(results[1] is DataState.ApiError && (results[1] as DataState.ApiError).apiErrorException == apiErrorException)
//        assertTrue(results[2] is DataState.Loading && (results[2] as DataState.Loading))
    }

    
    @Test
    fun datastateerrorflow_should_emit_error_state() = runTest {
        
        val exception = RuntimeException("Error")
        val requestId = 1

        
        val result = dataStateErrorFlow<String>(exception, requestId).filterError().first()

        
        assertTrue(result.throwable == exception)
    }

    
    @Test
    fun datastateapierrorflow_should_emit_apierror_state() = runTest {
        
        val apiErrorException = createApiErrorException()
        val requestId = 1

        
        val result = dataStateApiErrorFlow<String>(apiErrorException, requestId).filterApiError().first()

        
        assertTrue(result.apiErrorException == apiErrorException)
    }

    
    @Test
    fun datastatesuccessflow_should_emit_success_state_with_data() = runTest {
        
        val requestId = 1

        
        val result = dataStateSuccessFlow(requestId) { "data" }.filterSuccess().first()

        
        assertTrue(result.data == "data")
    }

    @Test
    fun datastatesuccessflow_should_emit_success_state_with_null_data() = runTest {
        
        val requestId = 1

        
        val result = dataStateSuccessFlow<String?>(requestId) { null }.filterSuccess().first()

        
        assertTrue(result.data == null)
    }

    
    @Test
    fun datastatedatamodelsuccessflow_should_emit_success_state_with_datamodel() = runTest {
        
        val requestId = 1

        
        val result = dataStateDataModelSuccessFlow(requestId) { "data" }.filterSuccess().first()

        
        assertTrue(result.data?.data == "data")
    }

    @Test
    fun api_error_should_return_correct_message() {
        val mockApiErrorException = mockk<ApiErrorException>()
        val mockErrorModel = mockk<ErrorModel>()
        every { mockApiErrorException.errorModel } returns mockErrorModel
        every { mockErrorModel.message() } returns "API error occurred"

        val apiError = DataState.ApiError(mockApiErrorException, -1)

        assertEquals("API error occurred", apiError.error)
    }

    @Test
    fun generic_error_should_return_correct_message() {
        val throwable = Throwable("Generic error occurred")
        val errorState = DataState.Error(throwable, -1)

        assertEquals("Generic error occurred", errorState.error)
    }

    @Test
    fun error_message_should_return_api_error_message() {
        val mockApiErrorException = mockk<ApiErrorException>()
        val mockErrorModel = mockk<ErrorModel>()
        every { mockApiErrorException.errorModel } returns mockErrorModel
        every { mockErrorModel.message() } returns "API error occurred"

        val apiError = DataState.ApiError(mockApiErrorException, -1)

        assertEquals("API error occurred", apiError.errorMessage)
    }

    @Test
    fun error_message_should_return_generic_error_message() {
        val throwable = Throwable("Generic error occurred")
        val errorState = DataState.Error(throwable, -1)

        assertEquals("Generic error occurred", errorState.errorMessage)
    }

    @Test
    fun error_message_should_return_null_for_other_states() {
        val successState = DataState.Success("Success data", -1)
        assertEquals(null, successState.errorMessage)
    }
}