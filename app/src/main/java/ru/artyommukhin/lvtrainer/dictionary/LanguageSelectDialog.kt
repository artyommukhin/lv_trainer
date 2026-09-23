package ru.artyommukhin.lvtrainer.dictionary

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import ru.artyommukhin.lvtrainer.R
import ru.artyommukhin.lvtrainer.dictionary.data.Language

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectDialog(
    selectedLanguage: Language,
    allLanguages: List<Language>,
    onDismissRequest: () -> Unit,
    onSelect: (Language) -> Unit,
) {
    val selectedLanguageIndex = allLanguages.indexOf(selectedLanguage).takeIf { it != -1 } ?: 0
    val lazyColumnState =
        rememberLazyListState(initialFirstVisibleItemIndex = selectedLanguageIndex)

    var inputValue by rememberSaveable { mutableStateOf("") }
    var filteredLanguages by rememberSaveable { mutableStateOf(allLanguages) }

    val inputFocusRequester = remember { FocusRequester() }

    BasicAlertDialog(
        onDismissRequest,
        properties = DialogProperties(),
    ) {
        Surface(
            modifier = Modifier.padding(vertical = 16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column {
                TextField(
                    modifier = Modifier.fillMaxWidth().focusRequester(inputFocusRequester),
                    value = inputValue,
                    onValueChange = { value ->
                        inputValue = value
                        filteredLanguages = allLanguages.filter { language ->
                            val words =
                                language.locale.displayLanguage
                                    .lowercase()
                                    .split(" ", "-")
                                    .map(String::trim)

                            words.any { it.startsWith(value) }
                        }
                    },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            painter = painterResource(R.drawable.search),
                            contentDescription = "Search language",
                        )
                    },
                )

                LazyColumn(state = lazyColumnState) {
                    items(filteredLanguages) { language ->
                        ListItem(
                            modifier =
                                Modifier.clickable {
                                    onSelect(language)
                                    onDismissRequest()
                                },
                            leadingContent = {
                                Text(language.symbol, modifier = Modifier.width(36.dp))
                            },
                            headlineContent = { Text(language.locale.displayLanguage) },
                            trailingContent = { Text(language.tag) },
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(1) {
        inputFocusRequester.requestFocus()
    }
}
