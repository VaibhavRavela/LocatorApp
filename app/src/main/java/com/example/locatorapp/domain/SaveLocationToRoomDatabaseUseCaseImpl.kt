package com.example.locatorapp.domain

import com.example.locatorapp.data.LocationDao
import com.example.locatorapp.data.LocationRepository
import javax.inject.Inject

class SaveLocationToRoomDatabaseUseCaseImpl @Inject constructor(private val locationRepository: LocationRepository) : SaveLocationToRoomDatabaseUseCase {
    override suspend fun saveLocationToRoomDatabase(location: Location): Long {
        return locationRepository.saveLocationToRoomDatabase(location)
    }
}