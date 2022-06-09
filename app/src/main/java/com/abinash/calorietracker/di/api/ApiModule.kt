package com.abinash.calorietracker.di.api

import android.content.Context
import android.net.ConnectivityManager
import com.abinash.calorietracker.api.Api
import com.abinash.calorietracker.api.ApiClient
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@Suppress("DEPRECATION")
@Module
class ApiModule {

    @Provides
    fun provideApi(): Api {

        /**
         * Client for offline caching
         */
        val client =
            OkHttpClient.Builder().addInterceptor { chain: Interceptor.Chain ->
                val request = chain.request()

                chain.proceed(request)
            }.build()

        return Retrofit.Builder()
            .client(client)
            .baseUrl(ApiClient.baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build().create(Api::class.java)

    }


}