package com.example.locatorapp.domain

interface UploadLocationToFirestoreFromRoomDatabaseUseCase {
    suspend fun uploadLocationToFirestoreFromRoomDatabase(locationId: Long)
}