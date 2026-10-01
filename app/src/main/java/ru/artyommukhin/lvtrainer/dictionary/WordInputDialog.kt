package ru.artyommukhin.lvtrainer.dictionary

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.intl.LocaleList
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import ru.artyommukhin.lvtrainer.dictionary.data.DictionaryWord
import ru.artyommukhin.lvtrainer.dictionary.data.Language

@Composable
fun WordInputDialog(
    wordToEdit: DictionaryWord? = null,
    onDismissRequest: () -> Unit,
    onConfirmation:
        (
            word: String,
            translation: String,
            wordLanguage: Language,
            translationLanguage: Language,
        ) -> Unit,
    viewModel: WordInputViewModel =
        hiltViewModel<WordInputViewModel, WordInputViewModel.Factory>(
            key = wordToEdit?.id?.toString(),
            creationCallback = { it.create(wordToEdit) },
        ),
) {
  var word by
      rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(wordToEdit?.word.orEmpty()))
      }
  var translation by
      rememberSaveable(stateSaver = TextFieldValue.Saver) {
        mutableStateOf(TextFieldValue(wordToEdit?.translation.orEmpty()))
      }

  val wordInteractionSource = remember { MutableInteractionSource() }
  val isWordFocused by wordInteractionSource.collectIsFocusedAsState()
  val translationInteractionSource = remember { MutableInteractionSource() }
  val isTranslationFocused by translationInteractionSource.collectIsFocusedAsState()
  val wordFocusRequester = remember { FocusRequester() }
  val translationFocusRequester = remember { FocusRequester() }

  val wordLanguage by viewModel.wordLanguage.collectAsStateWithLifecycle()
  var showWordLanguageSelectDialog by rememberSaveable { mutableStateOf(false) }

  val nativeLanguage by viewModel.translationLanguage.collectAsStateWithLifecycle()
  var showNativeLanguageSelectDialog by rememberSaveable { mutableStateOf(false) }

  fun isInputValid() = word.text.isNotBlank() && translation.text.isNotBlank()

  fun onSubmit() {
    if (!isInputValid()) return
    onConfirmation(
        word.text.trim(),
        translation.text.trim(),
        wordLanguage,
        nativeLanguage,
    )
    onDismissRequest()
  }

  Dialog(
      onDismissRequest,
      properties = DialogProperties(dismissOnClickOutside = false),
  ) {
    Surface(shape = RoundedCornerShape(16.dp)) {
      Column(
          verticalArrangement = Arrangement.Center,
          horizontalAlignment = Alignment.CenterHorizontally,
      ) {
        Text(
            text = if (wordToEdit == null) "Добавить слово" else "Обновить слово",
            fontSize = TextUnit(24f, TextUnitType.Sp),
            modifier = Modifier.padding(16.dp),
        )
        OutlinedTextField(
            modifier = Modifier.focusRequester(wordFocusRequester),
            interactionSource = wordInteractionSource,
            value = word,
            onValueChange = { word = it },
            label = { Text("Слово") },
            singleLine = true,
            keyboardOptions =
                KeyboardOptions(
                    imeAction = ImeAction.Next,
                    hintLocales = LocaleList(Locale(wordLanguage.locale)),
                ),
            trailingIcon = {
              Text(
                  wordLanguage.symbol,
                  modifier = Modifier.clickable { showWordLanguageSelectDialog = true },
              )
            },
        )
        OutlinedTextField(
            modifier = Modifier.focusRequester(translationFocusRequester),
            interactionSource = translationInteractionSource,
            value = translation,
            onValueChange = { translation = it },
            label = { Text("Перевод") },
            singleLine = true,
            keyboardOptions =
                KeyboardOptions(
                    imeAction = if (isInputValid()) ImeAction.Done else ImeAction.Previous,
                    hintLocales = LocaleList(Locale(nativeLanguage.locale)),
                ),
            keyboardActions =
                KeyboardActions(
                    onDone = { onSubmit() },
                ),
            trailingIcon = {
              Text(
                  nativeLanguage.symbol,
                  modifier = Modifier.clickable { showNativeLanguageSelectDialog = true },
              )
            },
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

      if (showWordLanguageSelectDialog)
          LanguageSelectDialog(
              selectedLanguage = wordLanguage,
              allLanguages = viewModel.allLanguages,
              onDismissRequest = { showWordLanguageSelectDialog = false },
              onSelect = { viewModel.updateWordLanguage(it) },
          )

      if (showNativeLanguageSelectDialog)
          LanguageSelectDialog(
              selectedLanguage = nativeLanguage,
              allLanguages = viewModel.allLanguages,
              onDismissRequest = { showNativeLanguageSelectDialog = false },
              onSelect = { viewModel.updateNativeLanguage(it) },
          )

      LaunchedEffect(1) {
        if (isWordFocused) wordFocusRequester.requestFocus()
        if (isTranslationFocused) translationFocusRequester.requestFocus()
        if (!isWordFocused && !isTranslationFocused) wordFocusRequester.requestFocus()
      }
    }
  }
}
