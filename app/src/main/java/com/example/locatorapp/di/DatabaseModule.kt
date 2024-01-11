package com.example.locatorapp.di

import android.content.Context
import androidx.room.Room
import com.example.locatorapp.data.LocationDao
import com.example.locatorapp.data.LocationRepository
import com.example.locatorapp.data.LocatorDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideLocatorDatabase(@ApplicationContext context: Context): LocatorDatabase {
        return Room.databaseBuilder(
            context,
            LocatorDatabase::class.java,
            "locator_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideLocationDao(database: LocatorDatabase): LocationDao {
            return database.locationDao()
    }

    @Provides
    @Singleton
    fun provideLocationRepository(locationDao: LocationDao): LocationRepository {
        return LocationRepository(locationDao)
    }

}