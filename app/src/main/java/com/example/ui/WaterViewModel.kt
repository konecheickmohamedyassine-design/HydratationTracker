package com.example.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.WaterIntake
import com.example.data.WaterRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class WaterViewModel(private val repository: WaterRepository) : ViewModel() {

    val dailyGoalMl: Int = 2000

    val allIntakes: StateFlow<List<WaterIntake>> = repository.allIntakes
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val totalConsumedToday: StateFlow<Int> = allIntakes.map { list ->
        list.filter { isToday(it.timestamp) }.sumOf { it.amountMl }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    val remainingMlToday: StateFlow<Int> = totalConsumedToday.map { consumed ->
        val remaining = dailyGoalMl - consumed
        if (remaining < 0) 0 else remaining
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = dailyGoalMl
    )

    val progressPercent: StateFlow<Float> = totalConsumedToday.map { consumed ->
        (consumed.toFloat() / dailyGoalMl.toFloat()).coerceIn(0f, 1.5f)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0f
    )

    val streakDays: StateFlow<Int> = allIntakes.map { list ->
        calculateStreak(list)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 0
    )

    fun addWater(amountMl: Int) {
        viewModelScope.launch {
            repository.addIntake(amountMl)
        }
    }

    fun deleteIntake(id: Int) {
        viewModelScope.launch {
            repository.deleteIntake(id)
        }
    }

    fun resetToday() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    private fun calculateStreak(list: List<WaterIntake>): Int {
        if (list.isEmpty()) return 0
        val dates = list.map {
            val cal = Calendar.getInstance().apply { timeInMillis = it.timestamp }
            "${cal.get(Calendar.YEAR)}-${cal.get(Calendar.MONTH)}-${cal.get(Calendar.DAY_OF_MONTH)}"
        }.toSet()

        var streak = 0
        val checkCal = Calendar.getInstance()
        val todayKey = "${checkCal.get(Calendar.YEAR)}-${checkCal.get(Calendar.MONTH)}-${checkCal.get(Calendar.DAY_OF_MONTH)}"

        checkCal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayKey = "${checkCal.get(Calendar.YEAR)}-${checkCal.get(Calendar.MONTH)}-${checkCal.get(Calendar.DAY_OF_MONTH)}"

        val startFromToday = dates.contains(todayKey)
        val startFromYesterday = dates.contains(yesterdayKey)

        if (!startFromToday && !startFromYesterday) {
            return 0
        }

        val currentCal = Calendar.getInstance()
        if (!startFromToday) {
            currentCal.add(Calendar.DAY_OF_YEAR, -1)
        }

        while (true) {
            val key = "${currentCal.get(Calendar.YEAR)}-${currentCal.get(Calendar.MONTH)}-${currentCal.get(Calendar.DAY_OF_MONTH)}"
            if (dates.contains(key)) {
                streak++
                currentCal.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streak
    }

    private fun isToday(timestamp: Long): Boolean {
        val cal1 = Calendar.getInstance().apply { timeInMillis = timestamp }
        val cal2 = Calendar.getInstance()
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    class Factory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WaterViewModel::class.java)) {
                val database = AppDatabase.getDatabase(context)
                val repository = WaterRepository(database.waterIntakeDao())
                @Suppress("UNCHECKED_CAST")
                return WaterViewModel(repository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
