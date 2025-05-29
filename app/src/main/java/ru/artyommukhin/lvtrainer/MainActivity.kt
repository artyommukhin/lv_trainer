package ru.artyommukhin.lvtrainer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable
import ru.artyommukhin.lvtrainer.dictionary.DictionaryPage
import ru.artyommukhin.lvtrainer.ui.theme.LVTrainerTheme

// Navigation routes
@Serializable
object Dictionary

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LVTrainerTheme {
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = Dictionary) {
                    composable<Dictionary> {
                        DictionaryPage()
                    }
                }
            }
        }
    }
}
