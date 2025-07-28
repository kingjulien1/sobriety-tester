package com.example.sobriety_tester

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sobriety_tester.ui.theme.GreenPrimary
import kotlinx.coroutines.launch
import kotlin.random.Random

// this is the number of dots that will appear during the reaction test
// users must press all of them to complete the test
const val REACTION_TEST_DOTS = 3

// this is the maximum score that can be achieved in the reaction test per dot
// the score is calculated based on the reaction time relative to the dot's appearance
const val MAX_SCORE_PER_DOT = 100

// this is the maximum reaction time in milliseconds
// that will be considered for scoring. Any reaction time above this will be clamped to this value.
const val MAX_REACTION_TIME_MS = 2000f

// this is the duration in milliseconds  the dot's sweep animation will take
const val DOT_SWEEP_DURATION_MS = 1000

private const val description = "Tap the green Circles as fast as you can"

/**
 * this test measures reaction time.
 * Users must press $REACTION_TEST_DOTS number of randomly appearing dots on the screen
 * A score will be calculated based on the average speed the dots had been pressed relative to their appearance
 * After test completion, results will be added to the viewmodel and displayed on the score screen
 *
 * @param navController The NavController to handle navigation between screens.
 * @param viewModel The ViewModel to manage app state and data.
 */
@Composable
fun ReactionTestScreen(navController: NavController, viewModel: AppViewModel) {
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current

    // get screen dimensions in pixels
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    // state variables
    var testStarted by remember { mutableStateOf(false) }
    var dotVisible by remember { mutableStateOf(false) }
    var dotOffset by remember { mutableStateOf(Offset.Zero) }

    // trial and score tracking
    var currentTrial by remember { mutableStateOf(0) }
    var totalScore by remember { mutableStateOf(0) }
    var reactionStartTime by remember { mutableStateOf(0L) }

    // animations for dot appearance
    val scale = remember { Animatable(0f) }
    val alpha = remember { Animatable(0f) }
    val sweepProgress = remember { Animatable(0f) }

    // ui dimensions and padding for the dot and layout
    val dotSize = 80.dp
    val margin = 16.dp
    val bottomPadding = 64.dp

    /**
     * Shows the next dot at a random position on the screen.
     * The dot will animate in and be clickable to record reaction time.
     */
    fun showNextDot() {
        // ensure we don't show more dots than allowed
        val dotSizePx = with(density) { dotSize.toPx() }
        val marginPx = with(density) { margin.toPx() }
        val bottomPaddingPx = with(density) { bottomPadding.toPx() }

        /**
         * We subtract 200px from the screen width and height to ensure
         * the dot appears within the visible area of the screen
         * and does not overlap with the bottom navigation or other UI elements.
         */
        val maxX = screenWidthPx - 200 - dotSizePx - marginPx
        val maxY = screenHeightPx - 200 - dotSizePx - marginPx - bottomPaddingPx

        // generate random position for the dot
        val x = marginPx + Random.nextFloat() * maxX
        val y = marginPx + Random.nextFloat() * maxY

        // update the dot's position and reset animations
        // using Offset to store the position in pixels
        dotOffset = Offset(x, y)

        // reset the trial start time
        reactionStartTime = System.currentTimeMillis()
        dotVisible = true

        // animate the dot's appearance
        scope.launch {
            sweepProgress.snapTo(0f)
            sweepProgress.animateTo(1f, tween(durationMillis = DOT_SWEEP_DURATION_MS))
            alpha.animateTo(1f, tween(300))
        }
    }

    if (!testStarted) {
        // show a countdown before starting the test
        CountdownScreen (
            nextTestDesc = description,
            onCountdownComplete = {
            // start the reaction test after countdown
            testStarted = true
            showNextDot()
        })
    } else {
        StandardLayout(
            subheading = "Test 1 of 3",
            heading = "${description}",
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // check if the dot is visible and animate it in if it is
                if (dotVisible) {
                    Canvas(
                        modifier = Modifier
                            .size(dotSize)
                            // center the dot in the box
                            .absoluteOffset(
                                x = with(density) { dotOffset.x.toDp() },
                                y = with(density) { dotOffset.y.toDp() }
                            )
                            .clickable(
                                // hide the ripple effect and other interactions when clicking the canvas
                                interactionSource = remember { MutableInteractionSource()  },
                                indication = null
                            ) {
                                // record reaction time and calculate score
                                val reactionTime = System.currentTimeMillis() - reactionStartTime
                                // clamp the reaction time to the maximum allowed
                                val clampedTime =
                                    reactionTime.toFloat().coerceIn(0f, MAX_REACTION_TIME_MS)
                                // calculate score based on reaction time, the faster the reaction, the higher the score
                                val score =
                                    ((1f - (clampedTime / MAX_REACTION_TIME_MS)) * 50 + 50).toInt()
                                totalScore += score
                                currentTrial++

                                // reset animations and animate the dot out
                                scope.launch {
                                    // reset the dot visibility and animations after a short delay
                                    dotVisible = false

                                    if (currentTrial == REACTION_TEST_DOTS) {
                                        // test completed, record score and navigate to score screen
                                        viewModel.recordTestScore(TestType.Reaction, totalScore)
                                        viewModel.persistScore(totalScore)
                                        navController.navigate("reaction_score_screen")
                                    } else showNextDot()
                                }
                            }
                    ) {
                        drawArc(
                            color = GreenPrimary,
                            startAngle = -90f,
                            sweepAngle = 360f * sweepProgress.value,
                            useCenter = false,
                            // draw the arc with a stroke style
                            style = Stroke(
                                width = size.minDimension * 0.2f,
                                cap = StrokeCap.Round
                            )
                        )
                    }
                }
            }
        }
    }
}
