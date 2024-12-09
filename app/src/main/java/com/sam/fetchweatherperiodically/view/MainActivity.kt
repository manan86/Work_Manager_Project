package com.sam.fetchweatherperiodically.view

import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import android.Manifest
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.sam.fetchweatherperiodically.databinding.ActivityMainBinding
import com.sam.fetchweatherperiodically.workers.WeatherFetcher
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var periodicWorkRequest: PeriodicWorkRequest
    private lateinit var oneTimeWorkRequest: OneTimeWorkRequest
    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                Toast.makeText(this,"Permission Granted", Toast.LENGTH_SHORT).show()
            } else {
                requestPermission()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


                initViews()
                requestPermission()

    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun initViews() {
        with(binding){
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            periodicButton.setOnClickListener{
                periodicWorkRequest = PeriodicWorkRequest.Builder(WeatherFetcher::class.java,30,
                 TimeUnit.MINUTES)
                 .setConstraints(constraints)
                 .build()
                WorkManager.getInstance(this@MainActivity).enqueue(periodicWorkRequest)
            }

            fetchNow.setOnClickListener{
                oneTimeWorkRequest = OneTimeWorkRequestBuilder<WeatherFetcher>()
                    .setConstraints(constraints)
                    .build()
                WorkManager.getInstance(this@MainActivity).enqueue(oneTimeWorkRequest)

                WorkManager.getInstance(this@MainActivity).getWorkInfoByIdLiveData(oneTimeWorkRequest.id).observe(this@MainActivity,
                    Observer{workInfo->
                        when(workInfo.state){
                            WorkInfo.State.SUCCEEDED->{
                                showToast(workInfo.outputData.toString())
                            }

                            WorkInfo.State.ENQUEUED -> {

                            }
                            WorkInfo.State.RUNNING -> {
                                showToast(workInfo.progress.toString())
                            }
                            WorkInfo.State.FAILED -> {}
                            WorkInfo.State.BLOCKED -> {}
                            WorkInfo.State.CANCELLED -> {}
                        }
                    })
            }

            cancleTasks.setOnClickListener{

                if(::oneTimeWorkRequest.isInitialized){
                    WorkManager.getInstance(this@MainActivity).cancelWorkById(oneTimeWorkRequest.id)
                }
                if(::periodicWorkRequest.isInitialized){
                    WorkManager.getInstance(this@MainActivity).cancelWorkById(periodicWorkRequest.id)
                }
            }
        }
    }

    fun showToast(msg:String){
        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show()
    }
}