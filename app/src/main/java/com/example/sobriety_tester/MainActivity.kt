@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.sobriety_tester

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// SobrietyTesterApp is the main entry point for the sobriety tester app.
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            val appViewModel: AppViewModel = viewModel()

            SobrietyTestApp(navController, appViewModel)
        }
    }
}

/**
 * SobrietyTestApp is the main composable function that sets up the navigation
 * and screens for the sobriety tester app. It uses a NavHost to manage different
 * screens including the start screen, various tests, and the final result screen.
 *
 * @param navController The NavController to handle navigation between screens.
 * @param viewModel The ViewModel to manage app state and data.
 */
@Composable
fun SobrietyTestApp(navController: NavHostController, viewModel: AppViewModel) {
    NavHost(navController = navController, startDestination = "start") {
        composable("start") { StartScreen(navController) }
        composable("reaction_test") { ReactionTestScreen(navController, viewModel) }
        composable("memory_test") { MemoryTestScreen(navController, viewModel) }
        composable("balance_test") { BalanceTestScreen(navController, viewModel) }
        composable("reaction_score_screen") {
            val score by viewModel.lastTestScore.collectAsState()
            ScoreScreen(
                score = score,
                maxScore = MAX_SCORE_PER_DOT * REACTION_TEST_DOTS,
                nextRoute = "memory_test",
                navController = navController,
                title = "Reaction Test Score"
            )
        }
        composable("final_result") { FinalResultScreen(viewModel) }
    }
}

/**
 * StartScreen is the initial screen of the sobriety tester app.
 * It welcomes the user and provides an overview of the tests they will take.
 * It includes a button to start the reaction test.
 *
 * @param navController The NavController to navigate between screens.
 */
@Composable
fun StartScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 24.dp, end = 24.dp, top = 64.dp,   // pushes heading down
                bottom = 32.dp // pushes button up
            ),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Heading area
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Sobriety Test",
                style = MaterialTheme.typography.labelLarge.copy(color = Color.Gray)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Welcome!\nYou will take 3 short tests.",
                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                textAlign = TextAlign.Center
            )
        }

        // Centered content
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "1️⃣ Reaction Test\n2️⃣ Memory Test\n3️⃣ Balance Test",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // Bottom button
        GreenActionButton(
            text = "Start Tests",
            onClick = { navController.navigate("reaction_test") },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
