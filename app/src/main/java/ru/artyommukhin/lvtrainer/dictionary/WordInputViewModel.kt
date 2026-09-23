package ru.artyommukhin.lvtrainer.dictionary

import android.app.Application
import androidx.core.os.ConfigurationCompat
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
import ru.artyommukhin.lvtrainer.dictionary.data.Language
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class WordInputViewModel @Inject constructor(
    application: Application,
    private val dataStore: DataStore<Preferences>,
) : ViewModel() {

    private val defaultLocale =
        ConfigurationCompat.getLocales(application.resources.configuration)[0]
            ?: Locale.getDefault()

    val wordLanguage = dataStore.data.mapNotNull { data ->
        data[wordLocaleKey]?.let { Language(Locale.forLanguageTag(it)) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = Language(defaultLocale),
    )

    val nativeLanguage = dataStore.data.mapNotNull { data ->
        data[nativeLocaleKey]?.let { Language(Locale.forLanguageTag(it)) }
    }.stateIn(
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
