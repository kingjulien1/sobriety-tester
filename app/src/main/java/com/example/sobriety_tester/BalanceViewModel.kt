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

private const val duration = 600 // Run for ~5s at 60fps
const val MAX_BALANCE_SCORE = duration-100 //100% accurate for 300 frames

class BalanceViewModel(application: Application) : AndroidViewModel(application), SensorEventListener {

    //get the sensor manager to communicate with the sensor
    private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    //the offset/position of the dot on screen
    private val _dotPosition = MutableStateFlow(Offset(0f, 0f)) // x = left-right, y = up-down
    val dotPosition: StateFlow<Offset> = _dotPosition

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score

    //marks the end of the test
    private var _testDone = MutableStateFlow(false)
    val testDone: StateFlow<Boolean> = _testDone

    //marks a running test
    private var _isRunning = MutableStateFlow(false)
    val testRunning: StateFlow<Boolean> = _isRunning

    private var frameCount = 0

    //start the game - register the listener (tell the system you want to receive data)
    //and set the start values
    fun startGame() {
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        _isRunning.value = true
        _score.value = 0
        frameCount = 0
    }

    //test done - unregister the listener (tell the system you don't want to receive data anymore)
    fun stopGame() {
        _isRunning.value = false
        sensorManager.unregisterListener(this)
        _testDone.value = true
    }

    //needed so the tests can be done on loop,
    //otherwise the test will be considered as done and will be skipped
    fun resetGame(){
        _testDone.value = false
    }

    override fun onSensorChanged(event: SensorEvent?) {
        //when the test is running and we are getting action/events/value changes from the sensor
        if (!_isRunning.value || event == null) return

        val x = event.values[0]  // left/right tilt
        val y = event.values[1]  // forward/backward tilt

        // Map the tilt to a screen offset (simplified and scaled)
        val scaledOffset = Offset(x * -50, y * 50)
        _dotPosition.value = scaledOffset

        // Score based on proximity to center (closer = better)
        // starting at frame 100 so the user can position first without losing to many points
        if(frameCount>=100) {
            val distance = scaledOffset.getDistance()
            val scoreDelta = max(0, (100 - distance).toInt())
            _score.value += scoreDelta
        }
        frameCount++

        //stop the game when the duration is reached
        if (frameCount >= duration) stopGame()
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onCleared() {
        stopGame()
    }
}
