package ru.artyommukhin.lvtrainer.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.artyommukhin.lvtrainer.database.AppDatabase
import ru.artyommukhin.lvtrainer.dictionary.DictionaryState.Loading
import ru.artyommukhin.lvtrainer.dictionary.DictionaryState.Success
import ru.artyommukhin.lvtrainer.dictionary.data.DictionaryWord
import javax.inject.Inject

sealed interface DictionaryState {
    data object Loading : DictionaryState
    data class Success(val words: List<DictionaryWord>) : DictionaryState
    data class Failure(val message: String) : DictionaryState
}

@HiltViewModel
class DictionaryViewModel @Inject constructor(
    database: AppDatabase,
) : ViewModel() {
    private val dictionary = database.dictionaryWordsDao()

    private val _state = MutableStateFlow<DictionaryState>(Loading)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val words = dictionary.getAll()
            _state.update { Success(words) }
        }
    }


    fun addWord(word: DictionaryWord) {
        viewModelScope.launch(Dispatchers.IO) {
            dictionary.insert(word)
            val words = dictionary.getAll()
            _state.update { Success(words) }
        }
    }

    fun removeWord(word: DictionaryWord) {
        viewModelScope.launch(Dispatchers.IO) {
            dictionary.delete(word)
            val words = dictionary.getAll()
            _state.update { Success(words) }
        }
    }
}