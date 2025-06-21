
package com.example.sobriety_tester

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.sobriety_tester.ui.theme.GreenPrimary


@Composable
fun CountdownTimer(
    seconds: Int = 3,
    onFinished: () -> Unit
) {
    var currentSecond by remember { mutableStateOf(seconds) }
    var elapsedMillis by remember { mutableStateOf(0f) }

    val animatedProgress by animateFloatAsState(
        targetValue = elapsedMillis / (seconds * 1000f),
        animationSpec = tween(durationMillis = 100),
        label = "progress"
    )

    // Text animation
    val scale = remember { Animatable(1f) }

    LaunchedEffect(Unit) {
        val totalMillis = seconds * 1000
        val tickInterval = 100L

        while (elapsedMillis < totalMillis) {
            delay(tickInterval)
            elapsedMillis += tickInterval
            val sec = seconds - (elapsedMillis / 1000).toInt()
            if (sec != currentSecond) {
                currentSecond = sec
                scale.snapTo(1.3f)
                scale.animateTo(1f, animationSpec = tween(300))
            }
        }

        currentSecond = 0
        onFinished()
    }

    Box(
        modifier = Modifier.size(150.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            progress = animatedProgress.coerceIn(0f, 1f),
            strokeWidth = 8.dp,
            modifier = Modifier.fillMaxSize(),
            color = GreenPrimary
        )
        Text(
            text = if (currentSecond > 0) "$currentSecond" else "Go!",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.scale(scale.value)
        )
    }
}

@Composable
fun CountdownScreen(onCountdownComplete: () -> Unit) {
    StandardLayout(
        subheading = "Get Ready",
        heading = "Test starts soon"
    ) {
        CountdownTimer(onFinished = onCountdownComplete)
    }
}