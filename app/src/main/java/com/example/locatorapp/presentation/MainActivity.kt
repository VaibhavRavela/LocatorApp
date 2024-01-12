package com.example.locatorapp.presentation

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
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
    private var isServiceRunning = false
    private val requestBackgroundLocationPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                startLocationService()
            }
        }
    private val requestForegroundLocationsPermissionsLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val fineLocationResult = result[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
            val coarseLocationResult = result[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false

            if (fineLocationResult && coarseLocationResult) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    requestBackgroundLocationPermissionsLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                } else {
                    startLocationService()
                }
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

    private fun checkForegroundPermissions(): Boolean {
        val fineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val coarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )

        if (fineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            coarseLocationPermission == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun checkPermissions(): Boolean {
        if (checkForegroundPermissions()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val backgroundLocationPermission = ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_BACKGROUND_LOCATION
                )

                if (backgroundLocationPermission == PackageManager.PERMISSION_GRANTED) {
                    return true
                }
                return false
            } else {
                return true
            }
        }
        return false
    }

    private fun startLocationService() {
        if (!checkPermissions()) {
            updateUI("permissions not granted")
            val permissionsToRequest = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            requestForegroundLocationsPermissionsLauncher.launch(permissionsToRequest)
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