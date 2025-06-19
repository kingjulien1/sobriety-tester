package com.example.sobriety_tester

import androidx.compose.ui.geometry.Offset
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch



@Composable
fun ReactionTestScreen(navController: NavController, viewModel: AppViewModel) {
    var testStarted by remember { mutableStateOf(false) }
    var dotVisible by remember { mutableStateOf(false) }
    var dotPosition by remember { mutableStateOf(IntOffset(0, 0)) }
    var reactionStartTime by remember { mutableStateOf(0L) }
    var currentTrial by remember { mutableStateOf(0) }
    var totalScore by remember { mutableStateOf(0) }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp

    fun showNextDot() {
        CoroutineScope(Dispatchers.Main).launch {
            dotVisible = false
            delay((1000..2000).random().toLong()) // wait before showing
            dotPosition = IntOffset(
                (50..screenWidth.value.toInt() - 100).random(),
                (150..screenHeight.value.toInt() - 200).random()
            )
            reactionStartTime = System.currentTimeMillis()
            dotVisible = true
        }
    }

    BaseScreen(title = "Reaction Test") { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            when {
                !testStarted -> {
                    CountdownTimer {
                        testStarted = true
                        showNextDot()
                    }
                }

                dotVisible -> {
                    Button(
                        onClick = {
                            val reactionTime = System.currentTimeMillis() - reactionStartTime
                            val score = (3000 - reactionTime).coerceAtLeast(0).toInt() / 10
                            totalScore += score
                            currentTrial++

                            if (currentTrial < 8) {
                                showNextDot()
                            } else {
                                viewModel.addScore(totalScore)
                                navController.navigate("score_screen")
                            }
                        },
                        modifier = Modifier.offset { dotPosition }
                    ) {
                        Text("🎯")
                    }
                }

                else -> {
                    Text("Wait for it...")
                }
            }
        }
    }
}
