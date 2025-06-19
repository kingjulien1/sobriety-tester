package com.example.sobriety_tester


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val scoreDao = AppDatabase.getDatabase(application).scoreDao()

    private val _totalScore = MutableStateFlow(0)
    val totalScore: StateFlow<Int> = _totalScore

    private val _lastTestScore = MutableStateFlow(0)
    val lastTestScore: StateFlow<Int> = _lastTestScore

    init {
        viewModelScope.launch {
            scoreDao.getTotalScore().collect {
                _totalScore.value = it ?: 0
            }
        }
    }

    fun addScore(points: Int) {
        _lastTestScore.value = points
        viewModelScope.launch {
            scoreDao.insert(Score(points = points))
        }
    }
}
