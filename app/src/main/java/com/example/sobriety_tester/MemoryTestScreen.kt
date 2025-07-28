package com.example.sobriety_tester

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sobriety_tester.ui.theme.GreenPrimary
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

// dot radius in dp
val DOT_SIZE = 80.dp

// amount of levels in the memory test, each level adds one more dot to the sequence & ends with $TOTAL_ROUNDS dos
const val TOTAL_ROUNDS = 5

// this is the maximum score that can be achieved in this memory test
const val MAX_MEMORY_SCORE = TOTAL_ROUNDS * 20

// padding values to ensure dots are not too close to the edges
const val SIDE_PADDING = 300
const val BOTTOM_PADDING = 500

private const val defaultdesc = "Repeat the Circle Sequence"
private const val altdesc = "Memorize the Sequence"

/**
 * MemoryTestScreen - a memory game composable that displays a sequence of animated dots
 * for the user to memorize and then reproduce in order. Tapping in the wrong order ends the test.
 * dots will appear in random positions on the screen in a sequence, each dot will animate a stroke sweep and disappears after a short delay.
 * the user must tap the dots in the same order they appeared, correct taps will make dots visible again and progress to the next round.
 *
 * @param navController used to navigate to the score screen
 * @param viewModel shared ViewModel for recording and storing test results
 */
@Composable
fun MemoryTestScreen(navController: NavController, viewModel: AppViewModel) {
    // remember the coroutine scope for launching animations and delays
    val scope = rememberCoroutineScope()
    // local screen dimensions and density for converting dp to pixels
    val density = LocalDensity.current
    val configuration = LocalConfiguration.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    // dot dimensions for calculating positions and sizes
    val dotSizePx = with(density) { DOT_SIZE.toPx() }
    val dotRadiusPx = dotSizePx / 2
    val sidePaddingPx = with(density) { SIDE_PADDING.toFloat() }
    val bottomPaddingPx = with(density) { BOTTOM_PADDING.toFloat() }

    // holding the sequence of dots to be memorized for the current round
    val sequence = remember { mutableStateListOf<Offset>() }
    // player input for the current round, storing the user's taps
    val playerInput = remember { mutableStateListOf<Offset>() }

    // game state tracking progress and user interaction
    var testStarted by remember { mutableStateOf(false) }
    // tracks the current round number / level, starting from 1
    var currentRound by remember { mutableIntStateOf(1) }
    // whether the sequence of dots is currently showcased to the user & user input not allowed
    var showingSequence by remember { mutableStateOf(false) }
    // whether the user is currently allowed to tap dots
    var userTurn by remember { mutableStateOf(false) }
    // currently highlighted dot for the sequence that the dots are animating in
    var highlightedDot by remember { mutableStateOf<Offset?>(null) }
    var isCompleted by remember { mutableStateOf(false) }

    // score tracking points earned by the user for this test
    var score by remember { mutableIntStateOf(0) }

    // animatable states for the dot sweep animation and tapped dots
    val dotSweepMap = remember { mutableStateMapOf<Offset, Float>() }

    var description by remember { mutableStateOf(altdesc) }

    /**
     * generates a random dot position on the screen, avoiding overlap with existing dots.
     * it will retry up to 100 times to find a valid position that does not overlap with existing dots.
     *
     * @param existingDots list of already placed dots to avoid overlap.
     * @return a random Offset representing the position of the new dot.
     */
    fun generateRandomDotAvoidingOverlap(existingDots: List<Offset>): Offset {
        // screen dimensions and padding in pixels for calculating valid positions
        val maxX = screenWidthPx - dotRadiusPx - sidePaddingPx
        val maxY = screenHeightPx - dotRadiusPx - bottomPaddingPx
        val minX = dotRadiusPx + sidePaddingPx
        val minY = dotRadiusPx + sidePaddingPx

        var candidate: Offset
        var attempts = 0

        // keep generating random positions until a valid one is found or max attempts reached
        // this ensures that the new dot does not overlap with existing dots
        do {
            val x = Random.nextFloat() * (maxX - minX) + minX
            val y = Random.nextFloat() * (maxY - minY) + minY
            candidate = Offset(x, y)
            attempts++
        } while (
        // check if the candidate position is too close to any existing dot
            existingDots.any { existing ->
                // calculate the distance between the candidate and existing dot
                // if the distance is less than 1.25 times the dot size, consider it overlapping
                (existing - candidate).getDistance() < dotSizePx * 1.25f
            } && attempts < 100
        )

        return candidate
    }

    /**
     * showcases the sequence of dots of the current level  by animating each dot in the sequence.
     * it highlights each dot in the sequence, animates a stroke sweep effect,
     * and waits for a short delay before moving to the next dot.
     */
    fun playSequence() {
        //tell user to wait and memorize
        description = altdesc
        showingSequence = true
        // turn off user input while the sequence is being shown
        userTurn = false
        // clear previous player taps from the last round
        playerInput.clear()

        // reset the dot sweep map animation values to start fresh
        scope.launch {
            for (dot in sequence) {
                // set the current dot to be highlighted and animate its stroke sweep
                highlightedDot = dot
                // reset the sweep value for the current dot to start the animation from 0
                dotSweepMap[dot] = 0f

                val anim = Animatable(0f)
                // animate the dot sweep from 0 to 1 over a specified duration
                // this will animate the stroke sweep effect around the dot
                anim.animateTo(1f, tween(durationMillis = DOT_SWEEP_DURATION_MS)) {
                    dotSweepMap[dot] = value
                }
                // delay to allow the animation to complete before moving to the next dot
                delay(150)
                // reset the highlighted dot after the animation completes
                highlightedDot = null
                // delay before showing the next dot in the sequence
                delay(200)
            }

            // after showing the entire sequence, reset the highlighted dot
            // this allows the user to start tapping the dots
            delay(300)
            showingSequence = false
            userTurn = true
            //tell the user they can input
            description = defaultdesc
        }
    }

    /**
     * Starts a new round / level of the memory test by generating a new sequence of dots.
     * If the current round exceeds the total rounds, it marks the test as completed
     * and navigates to the score screen with the final score.
     */
    fun startNewRound() {
        // check if the last level has been completed and if so, terminate the test
        if (currentRound > TOTAL_ROUNDS) {
            isCompleted = true
            // record the final score in the ViewModel and navigate to the score screen
            viewModel.recordTestScore(TestType.Memory, score)
            viewModel.persistScore(score)
            navController.navigate("memory_score_screen")
            return
        }

        // clear previous round data and generate a new sequence of dots
        sequence.clear()
        // generate a new sequence of $currentRound dots for the next round
        repeat(currentRound) { sequence.add(generateRandomDotAvoidingOverlap(sequence)) }
        // showcase this sequence to the user
        playSequence()
    }

    /**
     * Handles user taps on the screen, checking if the tap is within the expected dot's area.
     * If the tap is correct, it adds the tap to the player's input and checks if the sequence is complete.
     * If the tap is incorrect, it marks the test as completed and navigates to the score screen.
     *
     * @param tap The Offset where the user tapped on the screen.
     */
    fun handleUserTap(tap: Offset) {
        // check if it's the user's turn, if the test is completed or if the sequence is currently being shown
        if (!userTurn || isCompleted || showingSequence) return

        val expectedIndex = playerInput.size
        // get the expected dot for the current tap based on the number of taps made so far
        val expectedDot = sequence.getOrNull(expectedIndex) ?: return
        // calculate the distance between the tap and the expected dot
        val distance = (tap - expectedDot).getDistance()

        // check if the tap is within a permissible distance from the expected dot
        if (distance <= dotRadiusPx * 1.5f) {
            playerInput.add(tap)

            // check if this tap was the last expected dot in the sequence
            if (playerInput.size == sequence.size) {
                // if the user completed the level successfully, start the next round
                score += 20
                currentRound++
                userTurn = false
                // create and start a new level with a new sequence
                startNewRound()
            }
        } else {
            // tap was incorrect, test is terminated
            isCompleted = true
            // record the final score in the ViewModel and navigate to the score screen
            viewModel.recordTestScore(TestType.Memory, score)
            viewModel.persistScore(score)
            navController.navigate("memory_score_screen")
        }
    }

    // if the test has not started yet, show a countdown screen
    if (!testStarted) {
        CountdownScreen (
            nextTestDesc = defaultdesc,
            onCountdownComplete = {
            // start the first round after countdown completion
            testStarted = true
            startNewRound()
        })
    } else {
        StandardLayout(
            subheading = "Test 2 of 3",
            heading = "${description} - Level $currentRound"
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(userTurn) {
                        // listen for user taps on the screen
                        detectTapGestures { tapOffset -> handleUserTap(tapOffset) }
                    }
            ) {
                // iterate over the sequence of dots and draw them on the canvas
                sequence.forEach { dot ->
                    Canvas(
                        modifier = Modifier
                            .size(DOT_SIZE)
                            // calculate the absolute position of the dot based on its coordinates
                            .absoluteOffset(
                                x = with(density) { (dot.x - dotRadiusPx).toDp() },
                                y = with(density) { (dot.y - dotRadiusPx).toDp() }
                            )
                    ) {
                        // get the current dot's sweep value from the map to determine if it should be animated
                        val sweep = dotSweepMap[dot] ?: 0f

                        val tappedDots = (sequence.take(playerInput.size))
                        // check if the current dot was already tapped correctly by the user
                        // already correctly tapped dots will be made visible again
                        val hasAlreadyBeenTappedCorrectly =
                            tappedDots.any { it.x == dot.x && it.y == dot.y }

                        // check if the current dot is the one currently highlighted in the sequence
                        // or if the sweep is greater than 0 (meaning it is currently animating)
                        // or if it has been already tapped correctly by the user
                        if ((dot == highlightedDot || sweep > 0f) || hasAlreadyBeenTappedCorrectly) {
                            // draw the dot if it is currently showing the sequence
                            // or if it has been already tapped correctly by the user
                            if (sweep in 0f..0.99f || hasAlreadyBeenTappedCorrectly) {
                                drawArc(
                                    color = GreenPrimary,
                                    startAngle = -90f,
                                    sweepAngle = 360f * sweep,
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

                // show a message when the test is completed
                // TODO: kind of hacky, as it just flashes the message after test completion as we navigate away when $isCompleted is true
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
