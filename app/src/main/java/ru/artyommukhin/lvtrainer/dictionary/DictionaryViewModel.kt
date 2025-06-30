package ru.artyommukhin.lvtrainer.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.artyommukhin.lvtrainer.database.AppDatabase
import ru.artyommukhin.lvtrainer.dictionary.DictionaryState.Loading
import ru.artyommukhin.lvtrainer.dictionary.DictionaryState.NoWords
import ru.artyommukhin.lvtrainer.dictionary.DictionaryState.Success
import ru.artyommukhin.lvtrainer.dictionary.data.DictionaryWord
import javax.inject.Inject

sealed interface DictionaryState {
    data object Loading : DictionaryState
    data class Success(
        val untrainedWords: List<DictionaryWord>,
        val trainedWords: List<DictionaryWord>,
    ) : DictionaryState

    data class Failure(val message: String) : DictionaryState

    data object NoWords : DictionaryState
}

@HiltViewModel
class DictionaryViewModel @Inject constructor(
    database: AppDatabase,
) : ViewModel() {
    private val dictionary = database.dictionaryWordsDao()

    private val _state = MutableStateFlow<DictionaryState>(Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                dictionary.getAll(),
                dictionary.getAllUntrained(),
                dictionary.getAllTrained(),
            ) { all, untrained, trained ->
                if (all.isEmpty()) NoWords
                else Success(untrained, trained)
            }.collect { state ->
                _state.update { state }
            }
        }
    }

    fun addWord(word: DictionaryWord) {
        viewModelScope.launch {
            dictionary.insert(word)
        }
    }

    fun removeWord(word: DictionaryWord) {
        viewModelScope.launch {
            dictionary.delete(word)
        }
    }

    fun resetWordProgress(word: DictionaryWord) {
        viewModelScope.launch {
            dictionary.resetTrainCount(word.id)
        }
    }
}