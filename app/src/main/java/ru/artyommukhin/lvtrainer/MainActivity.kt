package ru.artyommukhin.lvtrainer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable
import ru.artyommukhin.lvtrainer.dictionary.DictionaryPage
import ru.artyommukhin.lvtrainer.training.TrainingPage
import ru.artyommukhin.lvtrainer.ui.theme.LVTrainerTheme

// Navigation routes
@Serializable
object Main

@Serializable
object Dictionary

@Serializable
object Training

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LVTrainerTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = Main) {
                    composable<Main> {
                        MainPage(
                            onNavigateToDictionary = { navController.navigate(Dictionary) },
                            onNavigateToTraining = { navController.navigate(Training) }
                        )
                    }
                    composable<Dictionary> {
                        DictionaryPage(
                            onNavigateBack = { navController.popBackStack() },
                        )
                    }
                    composable<Training> {
                        TrainingPage(
                            onNavigateBack = { navController.popBackStack() },
                            onNavigateToDictionary = {
                                navController.navigate(
                                    Dictionary,
                                    NavOptions.Builder()
                                        .setPopUpTo(Main::class, inclusive = false)
                                        .build(),
                                )
                            },
                        )
                    }
                }
            }
        }
    }
}
