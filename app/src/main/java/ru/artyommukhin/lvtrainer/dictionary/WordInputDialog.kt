package ru.artyommukhin.lvtrainer.dictionary

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.artyommukhin.lvtrainer.dictionary.data.Language

@Composable
fun WordInputDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (word: String, translation: String) -> Unit,
    viewModel: WordInputViewModel = hiltViewModel(),
) {
    var word by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var translation by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }

    // TODO: use MutableInteractionSource instead for tracking focused state
    var isWordFocused by rememberSaveable { mutableStateOf(false) }
    var isTranslationFocused by rememberSaveable { mutableStateOf(false) }
    val wordFocusRequester = remember { FocusRequester() }
    val translationFocusRequester = remember { FocusRequester() }

    val nativeLanguage by viewModel.nativeLanguage.collectAsStateWithLifecycle()
    var showNativeLanguageDropdown by rememberSaveable { mutableStateOf(false) }

    fun isInputValid() = word.text.isNotBlank() && translation.text.isNotBlank()

    fun onSubmit() {
        if (!isInputValid()) return
        onConfirmation(word.text.trim(), translation.text.trim())
        onDismissRequest()
    }

    Dialog(onDismissRequest = { onDismissRequest() }) {
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
                    modifier = Modifier
                        .focusRequester(wordFocusRequester)
                        .onFocusChanged {
                            if (it.isFocused) {
                                isWordFocused = true
                                isTranslationFocused = false
                            }
                        },
                    value = word,
                    onValueChange = { word = it },
                    label = { Text("Слово") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                    ),
                )
                OutlinedTextField(
                    modifier = Modifier
                        .focusRequester(translationFocusRequester)
                        .onFocusChanged {
                            if (it.isFocused) {
                                isTranslationFocused = true
                                isWordFocused = false
                            }
                        },
                    value = translation,
                    onValueChange = { translation = it },
                    label = { Text("Перевод") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        imeAction = if (isInputValid()) ImeAction.Done else ImeAction.Previous,
                        hintLocales = LocaleList(Locale(nativeLanguage.locale))
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onSubmit() },
                    ),
                    trailingIcon = {
                        Text(
                            nativeLanguage.symbol,
                            modifier = Modifier.clickable {
                                // TODO: navigate to language select page
                            },
                        )
                    }
                )
                // TODO: move language selection logic to a separate page
                LanguageSelectDropdown(
                    expanded = showNativeLanguageDropdown,
                    onExpandedChange = { showNativeLanguageDropdown = it },
                    value = nativeLanguage,
                    onValueChange = { viewModel.updateNativeLanguage(it) },
                    allLanguages = viewModel.allLanguages,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(
                        onClick = { onDismissRequest() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Отмена")
                    }
                    TextButton(
                        enabled = isInputValid(),
                        onClick = { onSubmit() },
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Text("Ок")
                    }
                }
            }
            LaunchedEffect(1) {
                if (isWordFocused) wordFocusRequester.requestFocus()
                if (isTranslationFocused) translationFocusRequester.requestFocus()
                if (!isWordFocused && !isTranslationFocused) wordFocusRequester.requestFocus()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelectDropdown(
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    value: Language,
    onValueChange: (Language) -> Unit,
    allLanguages: List<Language>,
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
    ) {
        OutlinedTextField(
            label = { Text("Язык") },
            value = value.toString(),
            onValueChange = { },
            readOnly = true,
            modifier = Modifier
                .menuAnchor(
                    type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                    enabled = true,
                ),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) },
        ) {
            for (language in allLanguages) {
                DropdownMenuItem(
                    onClick = {
                        onValueChange(language)
                        onExpandedChange(false)
                    },
                    text = { Text(language.toString()) }
                )
            }
        }
    }
}