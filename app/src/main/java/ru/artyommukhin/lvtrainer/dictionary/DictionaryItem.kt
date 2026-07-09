package ru.artyommukhin.lvtrainer.dictionary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.artyommukhin.lvtrainer.R
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
            modifier = Modifier
                .padding(end = 8.dp)
                .weight(1f),
            fontSize = 24.sp,
            text = "${word.word} - ${word.translation}",
        )

        Text("${word.trainCount * 20}%")

        IconButton(
            onClick = { onReset() }
        ) {
            Icon(
                painterResource(id = R.drawable.refresh),
                contentDescription = "Reset word progress"
            )
        }

        IconButton(
            onClick = { onDelete() }
        ) {
            Icon(
                painter = painterResource(id = R.drawable.delete),
                contentDescription = "Delete a word"
            )
        }
    }
}