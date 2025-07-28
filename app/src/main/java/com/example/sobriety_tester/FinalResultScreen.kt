package com.example.sobriety_tester

import android.widget.Space
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sobriety_tester.ui.theme.GreenPrimary
import com.example.sobriety_tester.ui.theme.RedPrimary
import kotlinx.coroutines.delay
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment

private const val threashhold = 80

/**
 * FinalResultScreen displays the user's scores after completing all test.
 * It shows the scores, the maximum possible scores, the relative percentages, and the overall score
 * and provides a button to navigate back to the start.
 */
@Composable
fun FinalResultScreen(navController: NavController, viewModel: AppViewModel) {
    val reactionScore = viewModel.reactionScore.collectAsState()
    val memoryScore = viewModel.memoryScore.collectAsState()
    val balanceScore = viewModel.balanceScore.collectAsState()

    val reactionPercentage = 100 * reactionScore.value / (MAX_SCORE_PER_DOT * REACTION_TEST_DOTS)
    val memoryPercentage = 100 * memoryScore.value / (MAX_MEMORY_SCORE)
    val balancePercentage = 100 * balanceScore.value / (MAX_BALANCE_SCORE)

    var total = reactionPercentage + memoryPercentage + balancePercentage

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
                text = "Testing Complete",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            Text(
                text = "Final Score",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 32.dp)
            )
        }
        Column (
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // content area with score indicator
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextOnlyScore(
                    test = "Reaction",
                    score = reactionScore.value,
                    total = MAX_SCORE_PER_DOT * REACTION_TEST_DOTS,
                    if(reactionPercentage < threashhold)RedPrimary else GreenPrimary
                ) //300
                TextOnlyScore(
                    test = "Memory",
                    score = memoryScore.value,
                    total = MAX_MEMORY_SCORE,
                    if(memoryPercentage < threashhold)RedPrimary else GreenPrimary
                ) //100
                TextOnlyScore(
                    test = "Balance",
                    score = balanceScore.value,
                    total = MAX_BALANCE_SCORE,
                    if(balancePercentage < threashhold)RedPrimary else GreenPrimary
                ) //300
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.headlineSmall,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(modifier = Modifier.height(8.dp))
                SimpleScoreIndicator(total, 300, if(total/3 < threashhold)RedPrimary else GreenPrimary)
            }
            Text(
                text = "you are ${if(total/3 < threashhold) "not " else ""}sober",
                style = MaterialTheme.typography.headlineSmall,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                color = if(total/3 < threashhold)RedPrimary else GreenPrimary
            )
        }
            GreenActionButton(
                text = "Return to start",
                onClick = { navController.navigate("start") },
                modifier = Modifier
                    .fillMaxWidth()
            )

    }
}

@Composable
fun TextOnlyScore(test: String, score: Int, total: Int, color: Color = GreenPrimary) {
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

    val animatedScore by animateIntAsState(
        targetValue = (animatedProgress.value * total).toInt(),
        animationSpec = tween(durationMillis = 1000),
        label = "scoreAnimation"
    )

    // simplified percentage calculation rounded to the nearest integer
    val percentage = (targetProgress * 100).toInt()
    Column (
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(
            text = "$test",
            style = MaterialTheme.typography.headlineSmall,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )

        Spacer(modifier = Modifier.height(8.dp))

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
                color = color
            )
        )
    }
}





