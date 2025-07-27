package com.example.sobriety_tester

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sobriety_tester.ui.theme.GreenPrimary
import kotlinx.coroutines.delay

/**
 * ScoreScreen displays the user's score after completing a test.
 * It shows the score, the maximum possible score, and the relative percentage
 * and provides a button to navigate to the next test.
 *
 * @param score The user's score to display.
 * @param maxScore The maximum possible score for the test.
 * @param nextRoute The route to navigate to when the user clicks the button.
 * @param navController The NavController to handle navigation.
 * @param title The title of the screen, default is "Your Score".
 * @param subtitle The subtitle of the screen, default is "Test Complete".
 */
@Composable
fun ScoreScreen(
    score: Int,
    maxScore: Int,
    nextRoute: String,
    navController: NavController,
    title: String = "Your Score",
    subtitle: String = "Test Complete"
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(start = 24.dp, end = 24.dp, top = 64.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // header area with title and subtitle
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
        // content area with score indicator
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            SimpleScoreIndicator(score = score, total = maxScore)
        }

        GreenActionButton(
            text = if(nextRoute== "final_result") "Final result" else"Next Test",
            onClick = { navController.navigate(nextRoute) },
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

/**
 * SimpleScoreIndicator displays a circular progress indicator and the score.
 * It animates the progress and score number after a short delay.
 *
 * @param score The user's score to display.
 * @param total The maximum possible score.
 */
@Composable
fun SimpleScoreIndicator(score: Int, total: Int) {
    // calculate the target progress based on score and total
    val targetProgress = score.toFloat() / total.toFloat()
    val animatedProgress = remember { mutableStateOf(0f) }

    // trigger animation 1 second after render
    LaunchedEffect(Unit) {
        // wait for 1 second before starting the animation
        // because of animations between screens, we want to give a little time
        // so the user can see the (entire) animation
        delay(1000)
        animatedProgress.value = targetProgress
    }

    // animate the progress value up to 'targetProgress'
    // this will animate the progress from 0 to the target value over 1 second
    val progress by animateFloatAsState(
        targetValue = animatedProgress.value,
        animationSpec = tween(durationMillis = 1000),
        label = "progressAnimation"
    )

    // animate the score value based on the progress
    // this will animate the score from 0 to the calculated score based on progress
    val animatedScore by animateIntAsState(
        targetValue = (animatedProgress.value * total).toInt(),
        animationSpec = tween(durationMillis = 1000),
        label = "scoreAnimation"
    )

    // simplified percentage calculation rounded to the nearest integer
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
                text = "$animatedScore / $total",
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