package ru.artyommukhin.lvtrainer.dictionary

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.artyommukhin.lvtrainer.di.LanguageModule
import ru.artyommukhin.lvtrainer.dictionary.data.DictionaryWord
import ru.artyommukhin.lvtrainer.dictionary.data.Language
import java.util.Locale
import javax.inject.Named

@HiltViewModel(assistedFactory = WordInputViewModel.Factory::class)
class WordInputViewModel
@AssistedInject
constructor(
    @Named(LanguageModule.DEFAULT_LOCALE) private val defaultLocale: Locale,
    private val dataStore: DataStore<Preferences>,
    @Assisted private val wordToEdit: DictionaryWord?,
) : ViewModel() {

  @AssistedFactory
  interface Factory {
    fun create(wordToEdit: DictionaryWord?): WordInputViewModel
  }

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

  companion object {
    val wordLocaleKey = stringPreferencesKey("wordLocale")
    val nativeLocaleKey = stringPreferencesKey("nativeLocale")
  }
}
