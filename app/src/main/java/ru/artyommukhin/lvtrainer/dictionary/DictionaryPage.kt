package ru.artyommukhin.lvtrainer.dictionary

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import ru.artyommukhin.lvtrainer.dictionary.data.DictionaryWord
import ru.artyommukhin.lvtrainer.R

enum class DictionaryPageTab(val label: String) {
    UNTRAINED("В процессе"),
    TRAINED("Изученные"),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryPage(
    onNavigateBack: () -> Unit,
    viewModel: DictionaryViewModel = hiltViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var openInputDialog by rememberSaveable { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = "Localized description"
                        )
                    }
                },
                title = { Text("Словарь") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { openInputDialog = true },
            ) {
                Icon(
                    painter = painterResource(R.drawable.add),
                    contentDescription = "Add a word"
                )
            }
        },
    ) { paddingValues ->
        when (val s = state) {
            DictionaryState.Loading -> {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                        .fillMaxSize()
                ) {
                    CircularProgressIndicator(Modifier.align(Alignment.Center))
                }
            }

            is DictionaryState.Failure -> {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(horizontal = 16.dp, vertical = 24.dp)
                        .fillMaxSize()
                ) {
                    Text("Ошибка загрузки", Modifier.align(Alignment.Center))
                }
            }

            is DictionaryState.Success -> {
                val pagerState = rememberPagerState(
                    initialPage = DictionaryPageTab.UNTRAINED.ordinal,
                    pageCount = { DictionaryPageTab.entries.size },
                )
                val selectedTab = remember { derivedStateOf { pagerState.currentPage } }
                val coroutineScope = rememberCoroutineScope()

                Column(
                    modifier = Modifier
                        .padding(paddingValues),
                ) {
                    PrimaryTabRow(
                        selectedTabIndex = selectedTab.value,
                    ) {
                        DictionaryPageTab.entries.forEachIndexed { index, tab ->
                            Tab(
                                selectedTab.value == index,
                                onClick = {
                                    coroutineScope.launch { pagerState.animateScrollToPage(index) }
                                },
                                text = { Text(tab.label) },
                            )
                        }
                    }

                    HorizontalPager(pagerState) { page ->
                        val tab = DictionaryPageTab.entries[page]
                        when (tab) {
                            DictionaryPageTab.UNTRAINED -> DictionaryWordList(
                                s.untrainedWords,
                                onResetWord = { word -> viewModel.resetWordProgress(word) },
                                onDeleteWord = { word -> viewModel.removeWord(word) },
                                emptyListText = "Все слова изучены"
                            )

                            DictionaryPageTab.TRAINED -> DictionaryWordList(
                                s.trainedWords,
                                onResetWord = { word -> viewModel.resetWordProgress(word) },
                                onDeleteWord = { word -> viewModel.removeWord(word) },
                                emptyListText = "Нет изученных слов"
                            )
                        }
                    }
                }
            }

            DictionaryState.NoWords -> {
                Box(
                    modifier = Modifier
                        .padding(paddingValues)
                        .padding(vertical = 24.dp, horizontal = 16.dp)
                        .fillMaxSize()
                ) {
                    Text("Пока здесь пусто", Modifier.align(Alignment.Center))
                }
            }
        }
    }

    if (openInputDialog) {
        WordInputDialog(
            onDismissRequest = { openInputDialog = false },
            onConfirmation = { word, translation ->
                viewModel.addWord(DictionaryWord(word = word, translation = translation))
            }
        )
    }
}
