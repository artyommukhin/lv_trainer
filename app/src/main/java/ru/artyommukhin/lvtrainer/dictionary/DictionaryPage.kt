package ru.artyommukhin.lvtrainer.dictionary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryPage(
    words: List<DictionaryWord>,
    onNavigateToAddWordDialog: () -> Unit,
    onDeleteWord: (index: Int) -> Unit
) {
    Scaffold(
        topBar = { TopAppBar(title = { Text("Словарь") }) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavigateToAddWordDialog() },
            ) {
                Icon(Icons.Default.Add, "Add a word")
            }
        },
    ) { paddingValues ->
        val columnState = rememberLazyListState()

        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize(),
            contentPadding = PaddingValues(vertical = 24.dp, horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            state = columnState,
        ) {
            itemsIndexed(words) { index, word ->
                DictionaryItem(
                    word,
                    onDelete = { onDeleteWord(index) },
                )
            }
        }
    }
}

@Composable
fun DictionaryItem(
    word: DictionaryWord,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            fontSize = TextUnit(24f, TextUnitType.Sp),
            text = "${word.word} - ${word.translation}",
        )
        IconButton(
            onClick = { onDelete() }
        ) {
            Icon(Icons.Default.Delete, "Delete a word")
        }
    }
}