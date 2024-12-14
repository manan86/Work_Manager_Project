package com.sam.fetchweatherperiodically.workers

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.sam.fetchweatherperiodically.model.dto.WeatherResponse
import com.sam.fetchweatherperiodically.model.remote.ApiClient
import com.sam.fetchweatherperiodically.model.remote.ApiService
import kotlinx.coroutines.runBlocking

class FetchWeatherWorker(context: Context, workerParameters: WorkerParameters) : Worker(context, workerParameters) {

    @SuppressLint("RestrictedApi")
    override fun doWork(): Result {
        setProgressAsync(workDataOf("progress" to 0))

        val apiService = ApiClient.getRetrofit().create(ApiService::class.java)

        return try {
            setProgressAsync(workDataOf("progress" to 50))

            // Use runBlocking to call the suspend function synchronously
            val response = runBlocking {
                apiService.getWeather()
            }

            setProgressAsync(workDataOf("progress" to 90))

            if (response.isSuccessful && response.body() != null) {
                val weatherResponse = response.body().toString()
                showNotification(response.body()!!)

                Result.success(Data.Builder().putString("data", weatherResponse).build())
            } else {
                Result.retry()
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
