package com.example.locatorapp.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LocationEntity::class], version = 1, exportSchema = false)
abstract class LocatorDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
}