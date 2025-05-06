package ru.artyommukhin.lvtrainer.dictionary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp

@Composable
fun WordAddDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (word: String, translation: String) -> Unit,
) {
    var word by rememberSaveable { mutableStateOf("") }
    var translation by rememberSaveable { mutableStateOf("") }

    Card(
        shape = RoundedCornerShape(16.dp),
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {

            Text(
                text = "Добавить слово",
                fontSize = TextUnit(24f, TextUnitType.Sp),
                modifier = Modifier.padding(16.dp),
            )
            OutlinedTextField(
                value = word,
                onValueChange = { word = it },
                label = { Text("Слово") },
            )
            OutlinedTextField(
                value = translation,
                onValueChange = { translation = it },
                label = { Text("Перевод") },
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                TextButton(
                    onClick = { onDismissRequest() },
                    modifier = Modifier.padding(8.dp),
                ) {
                    Text("Отмена")
                }
                TextButton(
                    onClick = { onConfirmation(word, translation) },
                    modifier = Modifier.padding(8.dp),
                ) {
                    Text("Ок")
                }
            }
        }
    }
}