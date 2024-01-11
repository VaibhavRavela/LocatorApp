package com.example.locatorapp.presentation

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.locatorapp.R
import com.example.locatorapp.presentation.service.LocationService
import com.google.firebase.FirebaseApp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var startButton: Button? = null
    private var stopButton: Button? = null
    private var feedbackTextView: TextView? = null
    private var isServiceRunning:Boolean = false
    private val requestLocationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {permissions ->
            val fineLocationGranted = permissions[android.Manifest.permission.ACCESS_FINE_LOCATION]?: false
            val coarseLocationGranted = permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION]?: false
            if (fineLocationGranted && coarseLocationGranted) {
                startLocationService()
            }
        }

    private fun stopLocationService() {
        val serviceIntent = Intent(this, LocationService::class.java)
        stopService(serviceIntent)
        isServiceRunning = false
        updateUI("stopped")
    }

    private fun updateUI(trackingStatus: String) {
        val statusText = when (trackingStatus) {
            "started" -> getString(R.string.tracking_started)
            "stopped" -> getString(R.string.tracking_stopped)
            "permissions not granted" -> getString(R.string.permissions_not_granted)
            else -> "Hi!"
        }
        feedbackTextView?.text = statusText
    }

    private fun checkPermissions(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            coarseLocationPermission == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun startLocationService() {
        if (!checkPermissions()) {
            updateUI("permissions not granted")
            val permissionsToRequest = arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
            requestLocationPermissionLauncher.launch(permissionsToRequest)
        } else {
            val serviceIntent = Intent(this, LocationService::class.java)
            startService(serviceIntent)
            isServiceRunning = true
            updateUI("started")
        }
    }

    private fun setupButtons() {
        startButton?.setOnClickListener {
            if (!isServiceRunning) {
                startLocationService()
            }
        }

        stopButton?.setOnClickListener {
            if (isServiceRunning) {
                stopLocationService()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)

        startButton = findViewById(R.id.startButton)
        stopButton = findViewById(R.id.stopButton)
        feedbackTextView = findViewById(R.id.feedbackTextView)

        setupButtons()
    }
}