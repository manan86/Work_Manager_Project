package com.sam.fetchweatherperiodically.model.remote

import com.sam.fetchweatherperiodically.BuildConfig
import com.sam.fetchweatherperiodically.model.dto.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface ApiService {

    @GET(Constants.END_POINT)
    suspend fun getWeather(@Query("q") query: String = "sunnyvale", @Header("key") apiKey:String= BuildConfig.API_KEY): retrofit2.Response<WeatherResponse>
}