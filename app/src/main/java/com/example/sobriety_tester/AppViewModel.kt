package com.example.sobriety_tester


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TestType {
    Reaction,
    Memory,
    Balance
}

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val scoreDao = AppDatabase.getDatabase(application).scoreDao()

    // Scores for individual tests
    private val _reactionScore = MutableStateFlow(0)
    val reactionScore: StateFlow<Int> = _reactionScore

    private val _memoryScore = MutableStateFlow(0)
    val memoryScore: StateFlow<Int> = _memoryScore

    private val _balanceScore = MutableStateFlow(0)
    val balanceScore: StateFlow<Int> = _balanceScore

    // Last completed test score (for score screen)
    private val _lastTestScore = MutableStateFlow(0)
    val lastTestScore: StateFlow<Int> = _lastTestScore

    // Total score from all tests (can also be from Room if needed)
    val totalScore: StateFlow<Int> = combine (
        reactionScore, memoryScore, balanceScore
    ) { reaction, memory, balance ->
        reaction + memory + balance
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    // Called after each test to set the relevant score
    fun recordTestScore(testType: TestType, score: Int) {
        when (testType) {
            TestType.Reaction -> _reactionScore.value = score
            TestType.Memory -> _memoryScore.value = score
            TestType.Balance -> _balanceScore.value = score
        }
        _lastTestScore.value = score
    }

    // Optional: persist each score in the DB
    fun persistScore(score: Int) {
        viewModelScope.launch {
            scoreDao.insert(Score(points = score))
        }
    }
}
