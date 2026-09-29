package ru.artyommukhin.lvtrainer.dictionary

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import ru.artyommukhin.lvtrainer.di.LanguageModule
import ru.artyommukhin.lvtrainer.dictionary.data.Language
import java.util.Locale
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class WordInputViewModel
@Inject
constructor(
    @Named(LanguageModule.DEFAULT_LOCALE) private val defaultLocale: Locale,
    private val dataStore: DataStore<Preferences>,
) : ViewModel() {

  val wordLanguage =
      dataStore.data
          .mapNotNull { data ->
            data[wordLocaleKey]?.let { Language(Locale.forLanguageTag(it)) }
          }
          .stateIn(
              scope = viewModelScope,
              started = SharingStarted.WhileSubscribed(),
              initialValue = Language(defaultLocale),
          )

  val nativeLanguage =
      dataStore.data
          .mapNotNull { data ->
            data[nativeLocaleKey]?.let { Language(Locale.forLanguageTag(it)) }
          }
          .stateIn(
              scope = viewModelScope,
              started = SharingStarted.WhileSubscribed(),
              initialValue = Language(defaultLocale),
          )

  val allLanguages = Locale.getAvailableLocales().map { Language(it) }

  fun updateWordLanguage(language: Language) {
    viewModelScope.launch {
      dataStore.edit { it[wordLocaleKey] = language.tag }
    }
  }

  fun updateNativeLanguage(language: Language) {
    viewModelScope.launch {
      dataStore.edit { it[nativeLocaleKey] = language.tag }
    }
  }

  companion object {
    val wordLocaleKey = stringPreferencesKey("wordLocale")
    val nativeLocaleKey = stringPreferencesKey("nativeLocale")
  }
}
