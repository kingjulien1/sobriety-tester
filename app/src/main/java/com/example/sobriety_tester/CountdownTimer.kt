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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import com.example.sobriety_tester.ui.theme.GreenPrimary
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import kotlin.math.roundToInt


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
}

@Composable
fun SimpleScoreIndicator(score: Int, total: Int) {
    val targetProgress = score.toFloat() / total.toFloat()
    val animatedProgress = remember { mutableStateOf(0f) }

    /**
     * because of the animation between screens, the animation of this progress bar will be over
     * before the screen is even visible, making the animation not visible. The delay makes sure the
     * component is rendered plus some delay to guarantee the animation is visible
     */
    LaunchedEffect(Unit) {
        delay(750)
        animatedProgress.value = targetProgress
    }

    val progress by animateFloatAsState(
        targetValue = animatedProgress.value,
        animationSpec = tween(durationMillis = 1000),
        label = "delayedProgressAnimation"
    )

    val percentage = (targetProgress * 100).toInt()

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(200.dp)
    ) {
        CircularProgressIndicator(
            progress = progress,
            strokeWidth = 20.dp,
            modifier = Modifier.fillMaxSize(),
            color = GreenPrimary
        )

        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "points",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.LightGray,
                    textAlign = TextAlign.Center,
                ),
            )
            Text(
                text = "$score / $total",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "$percentage%",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = GreenPrimary
                )
            )
        }
    }
}
