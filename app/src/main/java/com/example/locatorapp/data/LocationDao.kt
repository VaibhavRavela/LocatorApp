package com.example.locatorapp.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LocationDao {
    @Insert
    suspend fun insertLocation(locationEntity: LocationEntity): Long

    @Query("SELECT * FROM locations WHERE id = :locationId")
    suspend fun getLocationById(locationId: Long): LocationEntity?
}