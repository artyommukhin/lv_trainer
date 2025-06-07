package ru.artyommukhin.lvtrainer.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.artyommukhin.lvtrainer.database.AppDatabase
import ru.artyommukhin.lvtrainer.dictionary.data.DictionaryWord
import javax.inject.Inject

sealed interface TrainingUiState {
    data object Loading : TrainingUiState
    data object NoWords : TrainingUiState
    class Active(
        val word: DictionaryWord,
        val isAnsweredCorrectly: Boolean? = null,
        val isAnswered: Boolean = isAnsweredCorrectly != null,
    ) : TrainingUiState
}

@HiltViewModel
class TrainingViewModel @Inject constructor(
    database: AppDatabase,
) : ViewModel() {

    private val dictionary = database.dictionaryWordsDao()
    private val _state = MutableStateFlow<TrainingUiState>(TrainingUiState.Loading)
    val state = _state.asStateFlow()

    init {
        init()
    }

    private fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            var word = dictionary.getRandomWord()
                ?: return@launch _state.update { TrainingUiState.NoWords }
            val currentWord = (_state.value as? TrainingUiState.Active)?.word

            while (word == currentWord) word = dictionary.getRandomWord()!!

            _state.update { TrainingUiState.Active(word) }
        }
    }

    fun answer(word: DictionaryWord, translation: String) {
        _state.update {
            TrainingUiState.Active(word, word.translation == translation.trim())
        }
    }

    fun restart() {
        init()
    }
}