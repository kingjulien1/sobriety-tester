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

/**
 * Enum class representing the different types of tests in the Sobriety Tester app.
 * Each test type corresponds to a specific skill being evaluated.
 *
 * @property Reaction Represents the reaction time test.
 * @property Memory Represents the memory test.
 * @property Balance Represents the balance test.
 */
enum class TestType {
    Reaction,
    Memory,
    Balance
}

/**
 * ViewModel for the Sobriety Tester app.
 * Manages scores for different tests and provides methods to record and persist scores.
 * this ViewModel is tied to the application lifecycle.
 * It holds the scores for individual tests (reaction, memory, balance),
 * the last completed test score, and the total score across all tests.
 *
 * @param application The Application context, used to access the database.
 */
class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val scoreDao = AppDatabase.getDatabase(application).scoreDao()

    // scores for individual tests
    private val _reactionScore = MutableStateFlow(0)
    val reactionScore: StateFlow<Int> = _reactionScore

    private val _memoryScore = MutableStateFlow(0)
    val memoryScore: StateFlow<Int> = _memoryScore

    private val _balanceScore = MutableStateFlow(0)
    val balanceScore: StateFlow<Int> = _balanceScore

    // last completed test score (for score screen)
    private val _lastTestScore = MutableStateFlow(0)
    val lastTestScore: StateFlow<Int> = _lastTestScore

    // total score from all tests (can also be from Room if needed)
    val totalScore: StateFlow<Int> = combine(
        reactionScore, memoryScore, balanceScore
    ) { reaction, memory, balance ->
        reaction + memory + balance
    }.stateIn(viewModelScope, SharingStarted.Eagerly, 0)

    // called after each test to set the relevant score
    fun recordTestScore(testType: TestType, score: Int) {
        when (testType) {
            TestType.Reaction -> _reactionScore.value = score
            TestType.Memory -> _memoryScore.value = score
            TestType.Balance -> _balanceScore.value = score
        }
        _lastTestScore.value = score
    }

    // optional: persist each score in the DB
    // this can be called after each test or at the end of all tests
    fun persistScore(score: Int) {
        viewModelScope.launch {
            scoreDao.insert(Score(points = score))
        }
    }
}