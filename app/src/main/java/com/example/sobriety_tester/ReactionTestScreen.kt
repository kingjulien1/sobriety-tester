package com.example.sobriety_tester

import kotlin.random.Random
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import com.example.sobriety_tester.ui.theme.GreenPrimary
import kotlinx.coroutines.launch

const val REACTION_TEST_DOTS = 3
const val MAX_SCORE_PER_DOT = 100
const val BASE_REACTION_TIME_MS = 1000
const val SCORE_DIVISOR = 10

@Composable
fun ReactionTestScreen(navController: NavController, viewModel: AppViewModel) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    // Screen size in px
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    // State
    var testStarted by remember { mutableStateOf(false) }
    var dotVisible by remember { mutableStateOf(false) }
    var dotOffset by remember { mutableStateOf(Offset.Zero) }

    var currentTrial by remember { mutableStateOf(0) }
    var totalScore by remember { mutableStateOf(0) }
    var reactionStartTime by remember { mutableStateOf(0L) }

    // Animation
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }

    // UI constants
    val dotSize = 80.dp
    val margin = 16.dp
    val bottomPadding = 64.dp

    fun showNextDot() {
        val dotSizePx = with(density) { dotSize.toPx() }
        val marginPx = with(density) { margin.toPx() }
        val bottomPaddingPx = with(density) { bottomPadding.toPx() }

        val maxX = screenWidthPx - dotSizePx - marginPx
        val maxY = screenHeightPx - dotSizePx - marginPx - bottomPaddingPx

        val x = marginPx + Random.nextFloat() * maxX
        val y = marginPx + Random.nextFloat() * maxY

        dotOffset = Offset(x, y)
        reactionStartTime = System.currentTimeMillis()
        dotVisible = true

        scope.launch {
            scale.snapTo(0f)
            alpha.snapTo(0f)
            scale.animateTo(1f, tween(300))
            alpha.animateTo(1f, tween(300))
        }
    }

    // Layout
    StandardLayout(
        subheading = "Test 1 of 3",
        heading = "Follow the green dot"
    ) {
        if (!testStarted) {
            CountdownTimer {
                testStarted = true
                showNextDot()
            }
        } else {
            Box(modifier = Modifier.fillMaxSize()) {
                if (dotVisible) {
                    Box(
                        modifier = Modifier
                            .size(dotSize)
                            .absoluteOffset(
                                x = with(density) { dotOffset.x.toDp() },
                                y = with(density) { dotOffset.y.toDp() }
                            )
                            .scale(scale.value)
                            .alpha(alpha.value)
                            .clip(CircleShape)
                            .background(GreenPrimary)
                            .clickable {
                                val reactionTime = System.currentTimeMillis() - reactionStartTime

                                val MAX_REACTION_TIME_MS = 1500f
                                val clampedTime = reactionTime.toFloat().coerceIn(0f, MAX_REACTION_TIME_MS)

                                val score = ((1f - (clampedTime / MAX_REACTION_TIME_MS)) * MAX_SCORE_PER_DOT).toInt()
                                totalScore += score
                                currentTrial++

                                scope.launch {
                                    scale.animateTo(0f, tween(200))
                                    alpha.animateTo(0f, tween(200))
                                    dotVisible = false

                                    if (currentTrial == REACTION_TEST_DOTS) {
                                        viewModel.recordTestScore(TestType.Reaction, totalScore)
                                        viewModel.persistScore(totalScore)
                                        navController.navigate("reaction_score_screen")
                                    } else {
                                        showNextDot()
                                    }
                                }
                            }
                    )
                }
            }
        }
    }
}
