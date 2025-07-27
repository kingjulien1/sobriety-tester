package com.example.sobriety_tester

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.max

private const val duration = 300 // Run for ~5s at 60fps
const val MAX_BALANCE_SCORE = duration //100% accurate for 300 frames

class BalanceViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {

    private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private val _dotPosition = MutableStateFlow(Offset(0f, 0f)) // x = left-right, y = up-down
    val dotPosition: StateFlow<Offset> = _dotPosition

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score

    private var _testStarted = MutableStateFlow(false)
    val testStarted: StateFlow<Boolean> = _testStarted


    private var _testDone = MutableStateFlow(false)
    val testDone: StateFlow<Boolean> = _testDone

    private var `isRunning` = false
    private var frameCount = 0

    fun startGame() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        isRunning = true
        _testStarted.value = true
        _score.value = 0
        frameCount = 0
    }

    fun stopGame() {
        isRunning = false
        sensorManager.unregisterListener(this)
        _testDone.value = true
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!isRunning || event == null) return

        val x = event.values[0]  // left/right tilt
        val y = event.values[1]  // forward/backward tilt

        // Map the tilt to a screen offset (simplified and scaled)
        val scaledOffset = Offset(x * 50, y * 50)
        _dotPosition.value = scaledOffset

        // Score based on proximity to center (closer = better)
        val distance = scaledOffset.getDistance()
        val scoreDelta = max(0, (100 - distance).toInt())
        _score.value += scoreDelta
        frameCount++

        if (frameCount >= duration) stopGame()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onCleared() {
        stopGame()
    }
}
