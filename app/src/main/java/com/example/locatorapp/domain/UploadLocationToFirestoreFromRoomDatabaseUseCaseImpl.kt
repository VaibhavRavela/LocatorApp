package com.example.locatorapp.domain

import com.example.locatorapp.data.LocationRepository
import javax.inject.Inject

class UploadLocationToFirestoreFromRoomDatabaseUseCaseImpl @Inject constructor(private val locationRepository: LocationRepository) : UploadLocationToFirestoreFromRoomDatabaseUseCase {
    override suspend fun uploadLocationToFirestoreFromRoomDatabase(locationId: Long) {
        var location: Location? = locationRepository.getLocationFromRoomDatabase(locationId)
        locationRepository.uploadLocationToFirestore(location)
    }
}