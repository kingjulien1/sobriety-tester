package com.example.sobriety_tester

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlinx.coroutines.delay
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import kotlinx.coroutines.launch


private const val REACTION_TEST_DOTS = 8

@Composable
fun ReactionTestScreen(navController: NavController, viewModel: AppViewModel) {
    val dotSize = 80.dp
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    val scope = rememberCoroutineScope()

    var testStarted by remember { mutableStateOf(false) }
    var dotVisible by remember { mutableStateOf(false) }
    var dotX by remember { mutableStateOf(0.dp) }
    var dotY by remember { mutableStateOf(0.dp) }
    var reactionStartTime by remember { mutableStateOf(0L) }
    var currentTrial by remember { mutableStateOf(0) }
    var totalScore by remember { mutableStateOf(0) }

    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    fun showNextDot() {
        dotVisible = false

        // Random position within bounds
        val margin = 16.dp
        val safeWidth = screenWidth - dotSize - margin * 2
        val safeHeight = screenHeight - dotSize - margin * 2

        val offsetX = (0..safeWidth.value.toInt()).random().dp + margin
        val offsetY = (0..safeHeight.value.toInt()).random().dp + margin

        dotX = offsetX
        dotY = offsetY

        dotX = (0..dotX.value.toInt()).random().dp
        dotY = (0..dotY.value.toInt()).random().dp

        reactionStartTime = System.currentTimeMillis()
        dotVisible = true

        scope.launch {
            scale.snapTo(0f)
            alpha.snapTo(0f)
            scale.animateTo(1f, animationSpec = tween(300))
            alpha.animateTo(1f, animationSpec = tween(300))
        }
    }

    BaseScreen(title = "Reaction Test") { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (!testStarted) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CountdownTimer {
                        testStarted = true
                        showNextDot()
                    }
                }
            } else if (dotVisible) {
                Box(
                    modifier = Modifier
                        .size(dotSize)
                        .absoluteOffset(x = dotX, y = dotY)
                        .scale(scale.value)
                        .alpha(alpha.value)
                        .clip(CircleShape)
                        .background(Color(0xFF4CAF50))
                        .clickable {
                            val reactionTime = System.currentTimeMillis() - reactionStartTime
                            val score = (3000 - reactionTime).coerceAtLeast(0).toInt() / 10
                            totalScore += score
                            currentTrial++

                            scope.launch {
                                // animate out
                                scale.animateTo(0f, animationSpec = tween(200))
                                alpha.animateTo(0f, animationSpec = tween(200))
                                dotVisible = false

                                if (currentTrial < REACTION_TEST_DOTS) {
                                    showNextDot()
                                } else {
                                    viewModel.addScore(totalScore)
                                    navController.navigate("score_screen")
                                }
                            }
                        }
                )
            } else {
                Text("Wait for it...", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}

