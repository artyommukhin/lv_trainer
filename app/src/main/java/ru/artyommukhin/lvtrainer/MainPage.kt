package ru.artyommukhin.lvtrainer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.artyommukhin.lvtrainer.training.TrainingType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainPage(
    onNavigateToDictionary: () -> Unit,
    onNavigateToTraining: (type: TrainingType) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("LV Trainer") },
            )
        },
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 24.dp)
        ) {
            FilledTonalButton(
                onClick = { onNavigateToDictionary() },
            ) {
                Text("Словарь")
            }
            FilledTonalButton(
                onClick = { onNavigateToTraining(TrainingType.WORD_TO_TRANSLATION) },
            ) {
                Text("Тренировка \"слово-перевод\"")
            }
            FilledTonalButton(
                onClick = { onNavigateToTraining(TrainingType.TRANSLATION_TO_WORD) },
            ) {
                Text("Тренировка \"перевод-слово\"")
            }
        }
    }
}