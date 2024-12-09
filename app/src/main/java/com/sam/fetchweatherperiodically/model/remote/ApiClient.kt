package com.sam.fetchweatherperiodically.model.remote

import com.sam.fetchweatherperiodically.model.remote.Constants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private lateinit var retrofit: Retrofit

    fun getRetrofit(): Retrofit{
        if(!::retrofit.isInitialized){
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val httpClient = OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()

             retrofit = Retrofit.Builder().baseUrl(BASE_URL)
                 .addConverterFactory(GsonConverterFactory.create())
                 .client(httpClient)
                 .build()
        }
        return retrofit
    }
}