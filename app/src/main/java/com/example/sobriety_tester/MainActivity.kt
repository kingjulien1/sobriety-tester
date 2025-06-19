@file:OptIn(ExperimentalMaterial3Api::class)

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
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
    NavHost(navController = navController, startDestination = "start") {
        composable("start") { StartScreen(navController) }
        composable("reaction_test") { ReactionTestScreen(navController, viewModel) }
        composable("memory_test") { MemoryTestScreen(navController, viewModel) }
        composable("balance_test") { BalanceTestScreen(navController, viewModel) }
        composable("score_screen") { ScoreScreen(navController, viewModel) }
        composable("final_result") { FinalResultScreen(viewModel) }
    }
}

@Composable
fun BaseScreen(
    title: String,
    content: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) }
            )
        },
        content = content
    )
}

@Composable
fun StartScreen(navController: NavController) {
    BaseScreen(title = "Sobriety Tester") { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome! 🎉\nThis app will test your sobriety through 3 short tests.",
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "You will perform:\n\n1️⃣ A Reaction Test\n2️⃣ A Memory Test\n3️⃣ A Balance Test",
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(40.dp))
            Button(
                onClick = { navController.navigate("reaction_test") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Start Tests")
            }
        }
    }
}