package ru.artyommukhin.lvtrainer.dictionary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ru.artyommukhin.lvtrainer.dictionary.data.DictionaryWord

@Composable
fun DictionaryWordList(
    words: List<DictionaryWord>,
    onResetWord: (word: DictionaryWord) -> Unit,
    onDeleteWord: (word: DictionaryWord) -> Unit,
    ) {
    val columnState = rememberLazyListState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            start = 16.dp,
            end = 16.dp,
            top = 24.dp,
            bottom = 80.dp
        ),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        state = columnState,
    ) {
        items(words) { word ->
            DictionaryItem(
                word,
                onReset = { onResetWord(word) },
                onDelete = { onDeleteWord(word) },
            )
        }
    }
}