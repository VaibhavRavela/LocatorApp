package com.example.locatorapp.data

import android.util.Log
import com.example.locatorapp.domain.Location
import com.google.firebase.firestore.FirebaseFirestore

class LocationRepository(private val locationDao: LocationDao) {
    suspend fun saveLocationToRoomDatabase(location: Location): Long {
        val locationEntity = LocationEntity(
            latitude = location.latitude,
            longitude = location.longitude,
            timestamp = location.timestamp
        )
        return locationDao.insertLocation(locationEntity)
    }

    suspend fun getLocationFromRoomDatabase(locationId: Long): Location? {
        val locationEntity = locationDao.getLocationById(locationId)
        return locationEntity?.let {
            Location(
                latitude = it.latitude,
                longitude = it.longitude,
                timestamp = it.timestamp
            )
        }
    }
    
     fun uploadLocationToFirestore(location: Location?) {
        if (location != null) {
            val db = FirebaseFirestore.getInstance()
            val locationsCollection = db.collection("locations")
            val newLocation = hashMapOf(
                "latitude" to location.latitude,
                "longitude" to location.longitude,
                "timestamp" to location.timestamp
            )

            locationsCollection.add(newLocation)
                .addOnSuccessListener {
                    Log.d("locationsCollection", "Added Location Successfully!")
                }
                .addOnFailureListener {
                    Log.d("locationsCollection", "Failed To Add Location!")
                }
        } else {
            Log.d("uploadLocationToFirestore", "Location is Null!")
        }
    }
}