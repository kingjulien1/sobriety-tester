package com.example.sobriety_tester

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.sobriety_tester.ui.theme.GreenPrimary
import kotlin.math.roundToInt

private const val description = "Balance the green Dot in the Middle"

@Composable
fun BalanceTestScreen(navController: NavController, viewModel: AppViewModel, gameViewModel: BalanceViewModel) {
    val dotOffset by gameViewModel.dotPosition.collectAsState()
    val score by gameViewModel.score.collectAsState()
    val testRunning by gameViewModel.testRunning.collectAsState()
    val testDone by gameViewModel.testDone.collectAsState()

    if (!testRunning) {
        // show a countdown before starting the test
        CountdownScreen (
            nextTestDesc = description,
            onCountdownComplete = {gameViewModel.startGame()}
        )
    } else {
        StandardLayout(
            subheading = "Test 3 of 3",
            heading = description,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
            ) {

                // Center marker
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .align(Alignment.Center)
                        .background(Color.Black, shape = CircleShape)
                )

                // Moving dot based on sensor data
                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                x = dotOffset.x.roundToInt(),
                                y = dotOffset.y.roundToInt()
                            )
                        }
                        .align(Alignment.Center)
                        .size(50.dp)
                        .background(GreenPrimary, shape = CircleShape)
                )
                /*
                Text(
                    text = "Score: $score",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 32.dp)
                )
                */
            }
        }
    }
    //when the test is done - save the score and reset the test, then move to the score screen
    if (testDone) {
        viewModel.recordTestScore(TestType.Balance, score / 100)
        viewModel.persistScore(score)
        gameViewModel.resetGame()
        navController.navigate("balance_score_screen")
    }
}
