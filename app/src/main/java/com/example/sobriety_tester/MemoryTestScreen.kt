package com.example.sobriety_tester

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.example.sobriety_tester.ui.theme.GreenPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput

const val TOTAL_ROUNDS = 5
const val MAX_MEMORY_SCORE = TOTAL_ROUNDS * 20
val DOT_SIZE = 80.dp
const val SIDE_PADDING = 300
const val BOTTOM_PADDING = 500

@Composable
fun MemoryTestScreen(navController: NavController, viewModel: AppViewModel) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    val dotSizePx = with(density) { DOT_SIZE.toPx() }
    val dotRadiusPx = dotSizePx / 2
    val sidePaddingPx = with(density) { SIDE_PADDING}
    val bottomPaddingPx = with(density) { BOTTOM_PADDING}

    val sequence = remember { mutableStateListOf<Offset>() }
    val playerInput = remember { mutableStateListOf<Offset>() }

    var testStarted by remember { mutableStateOf(false) }
    var currentRound by remember { mutableStateOf(1) }
    var showingSequence by remember { mutableStateOf(false) }
    var userTurn by remember { mutableStateOf(false) }
    var score by remember { mutableStateOf(0) }
    var isCompleted by remember { mutableStateOf(false) }
    var highlightedDot by remember { mutableStateOf<Offset?>(null) }

    fun generateRandomDotAvoidingOverlap(existingDots: List<Offset>): Offset {
        val maxX = screenWidthPx - dotRadiusPx - SIDE_PADDING
        val maxY = screenHeightPx - dotRadiusPx - BOTTOM_PADDING
        val minX = dotRadiusPx + SIDE_PADDING
        val minY = dotRadiusPx + SIDE_PADDING

        var candidate: Offset
        var attempts = 0

        do {
            val x = Random.nextFloat() * (maxX - minX) + minX
            val y = Random.nextFloat() * (maxY - minY) + minY
            candidate = Offset(x, y)

            attempts++
        } while (
            existingDots.any { existing ->
                (existing - candidate).getDistance() < dotSizePx * 1.25f // generous padding
            } && attempts < 100
        )

        return candidate
    }


    fun playSequence() {
        showingSequence = true
        userTurn = false
        playerInput.clear()

        scope.launch {
            for (dot in sequence) {
                highlightedDot = dot
                delay(500)
                highlightedDot = null
                delay(250)
            }
            delay(300)
            showingSequence = false
            userTurn = true
        }
    }

    fun startNewRound() {
        if (currentRound > TOTAL_ROUNDS) {
            isCompleted = true
            viewModel.recordTestScore(TestType.Memory, score)
            viewModel.persistScore(score)
            navController.navigate("memory_score_screen")
            return
        }

        sequence.clear()
        repeat(currentRound) {
            sequence.add(generateRandomDotAvoidingOverlap(sequence))
        }
        playSequence()
    }

    fun handleUserTap(tap: Offset) {
        if (!userTurn || isCompleted || showingSequence) return

        val expectedIndex = playerInput.size
        val expectedDot = sequence.getOrNull(expectedIndex) ?: return
        val distance = (tap - expectedDot).getDistance()

        if (distance <= dotRadiusPx * 1.5f) {
            playerInput.add(tap)

            if (playerInput.size == sequence.size) {
                score += 20
                currentRound++
                userTurn = false
                startNewRound()
            }
        } else {
            isCompleted = true
            viewModel.recordTestScore(TestType.Memory, score)
            viewModel.persistScore(score)
            navController.navigate("memory_score_screen")
        }
    }

    if (!testStarted) {
        CountdownScreen {
            testStarted = true
            startNewRound()
        }
    } else {
        StandardLayout(
            subheading = "Test 2 of 3",
            heading = "Repeat the dot sequence - Level $currentRound"
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(userTurn) {
                        detectTapGestures { tapOffset ->
                            handleUserTap(tapOffset)
                        }
                    }
            ) {
                sequence.forEach { dot ->
                    Box(
                        modifier = Modifier
                            .size(DOT_SIZE)
                            .absoluteOffset(
                                x = with(density) { (dot.x - dotRadiusPx).toDp() },
                                y = with(density) { (dot.y - dotRadiusPx).toDp() }
                            )
                            .clip(CircleShape)
                            .background(if (dot == highlightedDot) GreenPrimary else Color.Gray)
                    )
                }

                if (isCompleted) {
                    Text(
                        text = "Test complete!",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }
    }
}
