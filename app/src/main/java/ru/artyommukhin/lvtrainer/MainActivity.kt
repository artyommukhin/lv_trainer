package ru.artyommukhin.lvtrainer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.NavOptions
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable
import ru.artyommukhin.lvtrainer.dictionary.DictionaryPage
import ru.artyommukhin.lvtrainer.training.TrainingPage
import ru.artyommukhin.lvtrainer.training.TrainingType
import ru.artyommukhin.lvtrainer.ui.theme.LVTrainerTheme

// Navigation routes
@Serializable
object Main

@Serializable
object Dictionary

@Serializable
data class Training(
    val type: TrainingType,
)

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
                            onNavigateToTraining = { type -> navController.navigate(Training(type)) },
                        )
                    }
                    composable<Dictionary> {
                        DictionaryPage(
                            onNavigateBack = { navController.popBackStack() },
                        )
                    }
                    composable<Training> { stackEntry ->
                        val route = stackEntry.toRoute<Training>()
                        TrainingPage(
                            trainingType = route.type,
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
