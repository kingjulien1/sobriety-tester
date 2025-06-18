package com.example.sobriety_tester

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.sobriety_tester.ui.theme.SobrietytesterTheme
import androidx.activity.compose.setContent
import androidx.compose.material3.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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

@Composable
fun SobrietyTestApp(navController: NavHostController, viewModel: AppViewModel) {
    NavHost(navController = navController, startDestination = "reaction_test") {
        composable("reaction_test") { ReactionTestScreen(navController, viewModel) }
        composable("memory_test") { MemoryTestScreen(navController, viewModel) }
        composable("balance_test") { BalanceTestScreen(navController, viewModel) }
        composable("score_screen") { ScoreScreen(navController, viewModel) }
        composable("final_result") { FinalResultScreen(viewModel) }
    }
}