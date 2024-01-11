package com.example.locatorapp.presentation.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.locatorapp.R
import com.example.locatorapp.domain.Location
import com.example.locatorapp.domain.SaveLocationToRoomDatabaseUseCase
import com.example.locatorapp.domain.UploadLocationToFirestoreFromRoomDatabaseUseCase
import com.example.locatorapp.presentation.MainActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LocationService @Inject constructor() : Service() {
    private lateinit var channel: NotificationChannel
    private lateinit var manager: NotificationManager
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val locationServiceJob = Job()
    private val iOScope = CoroutineScope(Dispatchers.IO + locationServiceJob)
    @Inject
    lateinit var saveLocationToRoomDatabaseUseCase: SaveLocationToRoomDatabaseUseCase
    @Inject
    lateinit var uploadLocationToFirestoreFromRoomDatabaseUseCase: UploadLocationToFirestoreFromRoomDatabaseUseCase

    companion object {
        const val CHANNEL_ID = "LocationServiceChannel"
        const val NOTIFICATION_ID = 1
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    private fun handleNewLocation(latitude: Double, longitude: Double) {
        val location = Location(latitude, longitude, System.currentTimeMillis())
        iOScope.launch {
            val locationId = saveLocationToRoomDatabaseUseCase.saveLocationToRoomDatabase(location)
            uploadLocationToFirestoreFromRoomDatabaseUseCase.uploadLocationToFirestoreFromRoomDatabase(locationId)
        }

    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val location = locationResult.lastLocation
            handleNewLocation(location.latitude, location.longitude)
        }
    }

    private fun createNotificationChannel() {
        channel = NotificationChannel(
            CHANNEL_ID,
            "Location Service Channel",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    private fun startLocationUpdates() {
        createNotificationChannel()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val locationRequest = LocationRequest.create().apply {
            interval = 60000
            fastestInterval = 60000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }

        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Tracking Location")
            .setContentText("LocatorApp is Tracking your Location")
            .setSmallIcon(R.drawable.baseline_location_on_black_24dp)
            .setContentIntent(pendingIntent)
            .build()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startLocationUpdates()
        return START_STICKY
    }

    override fun onDestroy() {
        stopLocationUpdates()
        locationServiceJob.cancel()
        stopForeground(STOP_FOREGROUND_DETACH)
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
