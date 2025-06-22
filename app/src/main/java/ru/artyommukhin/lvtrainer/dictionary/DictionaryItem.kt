package ru.artyommukhin.lvtrainer.dictionary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import ru.artyommukhin.lvtrainer.dictionary.data.DictionaryWord

@Composable
fun DictionaryItem(
    word: DictionaryWord,
    onReset: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            fontSize = 24.sp,
            text = "${word.word} - ${word.translation}",
        )

        Spacer(Modifier.weight(1f))

        Text("${word.trainCount * 20}%")

        IconButton(
            onClick = { onReset() }
        ) {
            Icon(Icons.Default.Refresh, "Reset word progress")
        }

        IconButton(
            onClick = { onDelete() }
        ) {
            Icon(Icons.Default.Delete, "Delete a word")
        }
    }
}