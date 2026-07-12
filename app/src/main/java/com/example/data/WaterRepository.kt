package com.example.data

import kotlinx.coroutines.flow.Flow

class WaterRepository(private val waterIntakeDao: WaterIntakeDao) {
    val allIntakes: Flow<List<WaterIntake>> = waterIntakeDao.getAllIntakesFlow()

    suspend fun addIntake(amountMl: Int, timestamp: Long = System.currentTimeMillis()) {
        val intake = WaterIntake(amountMl = amountMl, timestamp = timestamp)
        waterIntakeDao.insertIntake(intake)
    }

    suspend fun deleteIntake(id: Int) {
        waterIntakeDao.deleteIntakeById(id)
    }

    suspend fun clearAll() {
        waterIntakeDao.clearAllIntakes()
    }
}
