package ru.artyommukhin.lvtrainer.dictionary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.artyommukhin.lvtrainer.dictionary.data.DictionaryWord

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryPage(
    onNavigateBack: () -> Unit,
    viewModel: DictionaryViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val openInputDialog = rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Localized description"
                        )
                    }
                },
                title = { Text("Словарь") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { openInputDialog.value = true },
            ) {
                Icon(Icons.Default.Add, "Add a word")
            }
        },
    ) { paddingValues ->
        when (state) {
            DictionaryState.Loading -> {
                Text("Loading...")
            }

            is DictionaryState.Failure -> {
                Text("Failure")
            }

            is DictionaryState.Success -> {
                val words = (state as DictionaryState.Success).words
                val columnState = rememberLazyListState()

                LazyColumn(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 24.dp, horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    state = columnState,
                ) {
                    items(words) { word ->
                        DictionaryItem(
                            word,
                            onReset = { viewModel.resetWordProgress(word) },
                            onDelete = { viewModel.removeWord(word) },
                        )
                    }
                }
            }
        }
    }

    if (openInputDialog.value) {
        WordInputDialog(
            onDismissRequest = { openInputDialog.value = false },
            onConfirmation = { word, translation ->
                viewModel.addWord(DictionaryWord(word = word, translation = translation))
            }
        )
    }
}

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