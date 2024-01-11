package com.example.locatorapp.di

import com.example.locatorapp.data.LocationDao
import com.example.locatorapp.data.LocationRepository
import com.example.locatorapp.domain.SaveLocationToRoomDatabaseUseCase
import com.example.locatorapp.domain.SaveLocationToRoomDatabaseUseCaseImpl
import com.example.locatorapp.domain.UploadLocationToFirestoreFromRoomDatabaseUseCase
import com.example.locatorapp.domain.UploadLocationToFirestoreFromRoomDatabaseUseCaseImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideSaveLocationToRoomDatabaseUseCase(locationRepository: LocationRepository): SaveLocationToRoomDatabaseUseCase {
        return SaveLocationToRoomDatabaseUseCaseImpl(locationRepository)
    }

    @Provides
    @Singleton
    fun provideUploadLocationToFirestoreFromRoomDatabaseUseCase(locationRepository: LocationRepository): UploadLocationToFirestoreFromRoomDatabaseUseCase {
        return UploadLocationToFirestoreFromRoomDatabaseUseCaseImpl(locationRepository)
    }
}