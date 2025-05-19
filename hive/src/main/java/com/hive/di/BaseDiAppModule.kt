package com.hive.di

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStoreFile
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.MutableStateFlow
import java.util.Locale
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object BaseDiAppModule {

    @Singleton
    @Provides
    fun provideGson(): Gson {
        val gsonBuilder = GsonBuilder().serializeNulls()
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        return gsonBuilder.create()
    }


    @Singleton
    @Provides
    fun providesDataStore(application: Application): DataStore<Preferences> {
        return PreferenceDataStoreFactory.create(produceFile = {
            application.preferencesDataStoreFile(
                "data-store"
            )
        })
    }

    @Singleton
    @Provides
    fun providesLocale(): MutableStateFlow<Locale> {
        return MutableStateFlow(Locale.forLanguageTag("en-US"))
    }
}