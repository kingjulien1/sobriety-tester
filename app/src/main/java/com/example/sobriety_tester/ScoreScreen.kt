package com.example.sobriety_tester

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

@Composable
fun ScoreScreen(navController: NavController, viewModel: AppViewModel) {
    val lastScore by viewModel.lastTestScore.collectAsState()
    val maxPossibleScore = MAX_SCORE_PER_DOT * REACTION_TEST_DOTS
    val percentage = (lastScore / maxPossibleScore.toFloat()) * 100

    BaseScreen(title = "Test Result") { padding ->
        Column (
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Your Score: $lastScore",
                style = MaterialTheme.typography.headlineLarge
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Performance: ${"%.1f".format(percentage)}%",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(40.dp))
            Button (
                onClick = { navController.navigate("memory_test") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue to Memory Test")
            }
        }
    }
}
