package ru.artyommukhin.lvtrainer.dictionary

import android.database.sqlite.SQLiteConstraintException
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Locale
import javax.inject.Named
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.artyommukhin.lvtrainer.di.LanguageModule
import ru.artyommukhin.lvtrainer.dictionary.data.DictionaryWord
import ru.artyommukhin.lvtrainer.dictionary.data.DictionaryWordsRepository
import ru.artyommukhin.lvtrainer.dictionary.data.Language

data class WordInputFormState(
    val error: String? = null,
    val isSubmit: Boolean = false,
)

@HiltViewModel(assistedFactory = WordInputViewModel.Factory::class)
class WordInputViewModel
@AssistedInject
constructor(
    @Named(LanguageModule.DEFAULT_LOCALE) private val defaultLocale: Locale,
    private val dataStore: DataStore<Preferences>,
    @Assisted private val wordToEdit: DictionaryWord?,
    private val repository: DictionaryWordsRepository,
) : ViewModel() {

  @AssistedFactory
  interface Factory {
    fun create(wordToEdit: DictionaryWord?): WordInputViewModel
  }

  private val _formState = MutableStateFlow(WordInputFormState())
  val formState = _formState.asStateFlow()

  private val _wordLanguage = MutableStateFlow(Language(defaultLocale))
  val wordLanguage = _wordLanguage.asStateFlow()

  init {
    if (wordToEdit != null) _wordLanguage.update { Language(wordToEdit.wordLanguageTag) }
    else
        viewModelScope.launch {
          dataStore.data
              .mapNotNull { data -> data[wordLocaleKey]?.let { Language(it) } }
              .collectLatest { language -> _wordLanguage.update { language } }
        }
  }

  private val _translationLanguage = MutableStateFlow(Language(defaultLocale))
  val translationLanguage = _translationLanguage.asStateFlow()

  init {
    if (wordToEdit != null)
        _translationLanguage.update { Language(wordToEdit.translationLanguageTag) }
    else
        viewModelScope.launch {
          dataStore.data
              .mapNotNull { data -> data[nativeLocaleKey]?.let { Language(it) } }
              .collectLatest { language -> _translationLanguage.update { language } }
        }
  }

  val allLanguages = Locale.getAvailableLocales().map { Language(it) }

  fun updateWordLanguage(language: Language) {
    _wordLanguage.update { language }
    viewModelScope.launch {
      dataStore.edit { it[wordLocaleKey] = language.tag }
    }
  }

  fun updateNativeLanguage(language: Language) {
    _translationLanguage.update { language }
    viewModelScope.launch {
      dataStore.edit { it[nativeLocaleKey] = language.tag }
    }
  }

  fun addWord(word: DictionaryWord) {
    viewModelScope.launch {
      try {
        repository.addWord(word)
        _formState.update { it.copy(isSubmit = true) }
      } catch (_: SQLiteConstraintException) {
        _formState.update { it.copy(error = "Такое слово уже есть в словаре") }
      }
    }
  }

  fun updateWord(word: DictionaryWord) {
    viewModelScope.launch {
      try {
        repository.updateWord(word)
        _formState.update { it.copy(isSubmit = true) }
      } catch (_: SQLiteConstraintException) {
        _formState.update { it.copy(error = "Такое слово уже есть в словаре") }
      }
    }
  }

  fun resetError() {
    _formState.update { it.copy(error = null) }
  }

  fun resetState() {
    _formState.update { WordInputFormState() }
  }

  companion object {
    val wordLocaleKey = stringPreferencesKey("wordLocale")
    val nativeLocaleKey = stringPreferencesKey("nativeLocale")
  }
}
