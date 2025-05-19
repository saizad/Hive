package com.hive

import com.hive.model.BaseApiError


class ApiErrorException(val errorModel: BaseApiError) :
    Exception(Throwable(errorModel.message()))