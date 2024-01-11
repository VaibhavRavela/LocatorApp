package com.example.locatorapp.domain

interface SaveLocationToRoomDatabaseUseCase {
    suspend fun saveLocationToRoomDatabase(location: Location): Long
}