package ru.artyommukhin.lvtrainer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import kotlinx.serialization.Serializable
import ru.artyommukhin.lvtrainer.dictionary.DictionaryPage
import ru.artyommukhin.lvtrainer.dictionary.DictionaryWord
import ru.artyommukhin.lvtrainer.dictionary.WordAddDialog
import ru.artyommukhin.lvtrainer.ui.theme.LVTrainerTheme

// Navigation routes
@Serializable
object Dictionary

@Serializable
object AddWord

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LVTrainerTheme {
                val dictionary = remember {
                    mutableStateListOf(
                        DictionaryWord("abols", "яблоко"),
                        DictionaryWord("durvis", "дверь"),
                        DictionaryWord("logs", "окно"),
                    )
                }
                val navController = rememberNavController()

                NavHost(navController = navController, startDestination = Dictionary) {
                    composable<Dictionary> {
                        DictionaryPage(
                            words = dictionary,
                            onNavigateToAddWordDialog = {
                                navController.navigate(
                                    route = AddWord
                                )
                            },
                            onDeleteWord = { index -> dictionary.removeAt(index) },
                        )
                    }
                    dialog<AddWord>(
                        dialogProperties = DialogProperties()
                    ) {
                        WordAddDialog(
                            onDismissRequest = { navController.popBackStack() },
                            onConfirmation = { word, translation ->
                                dictionary.add(DictionaryWord(word, translation))
                                navController.popBackStack()
                            },
                        )
                    }
                }
            }
        }
    }
}
