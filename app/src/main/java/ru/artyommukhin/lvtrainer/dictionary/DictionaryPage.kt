package ru.artyommukhin.lvtrainer.dictionary

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryPage(
    words: Array<DictionaryWord>,
    onNavigateToAddWordDialog: () -> Unit,
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
            state = columnState,
        ) {
            items(words) { word -> DictionaryItem(word) }
        }
    }
}

@Composable
fun DictionaryItem(word: DictionaryWord) {
    Text(
        fontSize = TextUnit(24f, TextUnitType.Sp),
        text = "${word.word} - ${word.translation}",
    )
}