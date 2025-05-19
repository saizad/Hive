package com.hive

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class DataStoreWrapper @Inject constructor(
    val dataStore: DataStore<Preferences>,
    val gson: Gson
) {

    fun getValue(prefKey: String): Flow<String?> {
        val key = stringPreferencesKey(prefKey)
        return dataStore.data.map { pref ->
            pref[key]
        }
    }

    suspend fun putValue(prefKey: String, value: String?) {
        if (value == null) {
            remove(prefKey)
        } else {
            val key = stringPreferencesKey(prefKey)
            dataStore.edit {
                it[key] = value
            }
        }
    }

    fun <T> getObject(prefKey: String, classOfT: Class<T>): Flow<T?> {
        val key = stringPreferencesKey(prefKey)
        return dataStore.data.map {
            gson.fromJson(it[key], classOfT)
        }
    }

    suspend fun putObject(prefKey: String, value: Any?) {
        if (value == null) {
            remove(prefKey)
        } else {
            putValue(prefKey, gson.toJson(value))
        }
    }

    suspend fun remove(prefKey: String, listener: () -> Unit = {}) {
        val key = stringPreferencesKey(prefKey)
        dataStore.edit {
            it.remove(key)
            listener.invoke()
        }
    }

    suspend fun removeAll(): Preferences {
        return dataStore.edit {
            it.clear()
        }
    }

    fun booleanValue(prefKey: String): Flow<Boolean?> {
        val key = booleanPreferencesKey(prefKey)
        return dataStore.data.map { pref ->
            pref[key]
        }
    }

    suspend fun putValue(prefKey: String, value: Boolean?) {
        if (value == null) {
            remove(prefKey)
        } else {
            val key = booleanPreferencesKey(prefKey)
            dataStore.edit {
                it[key] = value
            }
        }
    }
}