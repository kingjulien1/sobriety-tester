package com.example.sobriety_tester

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.sobriety_tester.ui.theme.GreenPrimary
import kotlinx.coroutines.delay

/**
 * CountdownTimer is a composable that displays a countdown timer
 * with a circular progress indicator and animated text.
 * It counts down from a specified number of seconds
 * and triggers a callback when finished.
 *
 * @param seconds The number of seconds to count down from. Default is 3 seconds.
 * @param onFinished Callback function to invoke when the countdown reaches zero.
 */
@Composable
fun CountdownTimer(
    seconds: Int = 3,
    onFinished: () -> Unit,
    nextTestDesc: String
) {
    // state variables to track the current second and elapsed time
    var currentSecond by remember { mutableStateOf(seconds) }
    var elapsedMillis by remember { mutableStateOf(0f) }

    // animate the progress of the circular indicator based on elapsed time
    val animatedProgress by animateFloatAsState(
        targetValue = elapsedMillis / (seconds * 1000f),
        animationSpec = tween(durationMillis = 100),
        label = "progress"
    )

    // animatable scale for the text to create a pulse effect
    val scale = remember { Animatable(1f) }

    // use LaunchedEffect to run the countdown logic when the composable is first rendered
    // it will update the current second and animate the text scaling
    LaunchedEffect(Unit) {
        val totalMillis = seconds * 1000
        val tickInterval = 100L

        while (elapsedMillis < totalMillis) {
            // this delay simulates the countdown tick
            delay(tickInterval)
            // this will ensure the countdown is smooth and updates every tickInterval milliseconds
            elapsedMillis += tickInterval
            // calculate the current second based on elapsed time
            val sec = seconds - (elapsedMillis / 1000).toInt()
            // update the current second if it has changed
            if (sec != currentSecond) {
                currentSecond = sec
                // animate the scale of the text to create a pulse effect
                scale.snapTo(1.3f)
                scale.animateTo(1f, animationSpec = tween(300))
            }
        }

        currentSecond = 0
        onFinished()
    }
    Column (
        modifier = Modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly
    ){
        Text(
            text = nextTestDesc,
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center
        )
        Box(
            modifier = Modifier.size(200.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = animatedProgress.coerceIn(0f, 1f),
                strokeWidth = 20.dp,
                modifier = Modifier.fillMaxSize(),
                color = GreenPrimary
            )
            Text(
                text = if (currentSecond > 0) "$currentSecond" else "Go!",
                style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.scale(scale.value)
            )
        }
        Spacer(modifier = Modifier.width(1.dp))
    }
}

/**
 * CountdownScreen displays a countdown timer with a heading and subheading.
 * It uses the CountdownTimer composable and triggers a callback when the countdown finishes.
 *
 * @param onCountdownComplete Callback to invoke when the countdown completes.
 */
@Composable
fun CountdownScreen(onCountdownComplete: () -> Unit, nextTestDesc: String = "") {
    StandardLayout(
        subheading = "Get Ready",
        heading = "Test starts soon"
    ) {
        CountdownTimer(onFinished = onCountdownComplete, nextTestDesc = nextTestDesc)
    }
}
