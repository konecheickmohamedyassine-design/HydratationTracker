package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterIntakeDao {
    @Query("SELECT * FROM water_intake ORDER BY timestamp DESC")
    fun getAllIntakesFlow(): Flow<List<WaterIntake>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertIntake(intake: WaterIntake)

    @Query("DELETE FROM water_intake WHERE id = :id")
    suspend fun deleteIntakeById(id: Int)

    @Query("DELETE FROM water_intake")
    suspend fun clearAllIntakes()
}
