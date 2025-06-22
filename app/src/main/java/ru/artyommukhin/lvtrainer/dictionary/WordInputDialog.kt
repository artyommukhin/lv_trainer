package ru.artyommukhin.lvtrainer.dictionary

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
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
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun WordInputDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: (word: String, translation: String) -> Unit,
) {
    var word by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var translation by rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(""))
    }
    var isWordFocused by rememberSaveable { mutableStateOf(false) }
    var isTranslationFocused by rememberSaveable { mutableStateOf(false) }
    val wordFocusRequester = remember { FocusRequester() }
    val translationFocusRequester = remember { FocusRequester() }

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
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onSubmit() },
                    ),
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