package com.sam.fetchweatherperiodically.workers

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.sam.fetchweatherperiodically.model.dto.WeatherResponse
import com.sam.fetchweatherperiodically.model.remote.ApiClient
import com.sam.fetchweatherperiodically.model.remote.ApiService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class WeatherFetcher(context: Context, workerParameters: WorkerParameters) : CoroutineWorker(context,workerParameters) {
    private var isSuccessful : Boolean = false
    private var weatherResponse : String = ""

    @SuppressLint("RestrictedApi")
    override suspend fun doWork(): Result {

        setProgressAsync(workDataOf("progress" to 0))

        val apiService = ApiClient.getRetrofit().create<ApiService>(ApiService::class.java)

        setProgressAsync(workDataOf("progress" to 50))

        val job = CoroutineScope(Dispatchers.IO).async{
            val response = apiService.getWeather()
            if(response.isSuccessful && response.body()!=null) {
                setProgressAsync(workDataOf("progress" to 90))

                isSuccessful = true
                weatherResponse = response.body().toString()
                showNotification(response.body()!!)

            }else{
                setProgressAsync(workDataOf("progress" to 90))
                isSuccessful = false

            }
        }

        job.await()
        return if(isSuccessful){
            Result.Success(Data.Builder().putString("data",weatherResponse).build())
        }else{
            Result.retry()
        }

    }

    private fun showNotification(weather: WeatherResponse) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel(
            "weather_channel",
            "Weather Updates",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)

        val notification = NotificationCompat.Builder(applicationContext, "weather_channel")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentTitle("Weather in ${weather.location.name}")
            .setContentText("Temp: ${weather.current.temp_c}°C, ${weather.current}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)
    }


}