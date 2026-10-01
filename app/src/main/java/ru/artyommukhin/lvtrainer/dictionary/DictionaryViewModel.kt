package ru.artyommukhin.lvtrainer.dictionary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.artyommukhin.lvtrainer.dictionary.DictionaryState.Loading
import ru.artyommukhin.lvtrainer.dictionary.DictionaryState.NoWords
import ru.artyommukhin.lvtrainer.dictionary.DictionaryState.Success
import ru.artyommukhin.lvtrainer.dictionary.data.DictionaryWord
import ru.artyommukhin.lvtrainer.dictionary.data.DictionaryWordsRepository

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
class DictionaryViewModel
@Inject
constructor(
  val repository: DictionaryWordsRepository,
) : ViewModel() {

  private val _state = MutableStateFlow<DictionaryState>(Loading)
  val state = _state.asStateFlow()

  init {
    viewModelScope.launch {
      repository.words.collect { result ->
        val state =
            if (result.isEmpty) NoWords else Success(result.untrainedWords, result.trainedWords)
        _state.update { state }
      }
    }
  }

  fun addWord(word: DictionaryWord) {
    viewModelScope.launch {
      repository.addWord(word)
    }
  }

  fun updateWord(word: DictionaryWord) {
    viewModelScope.launch {
      repository.updateWord(word)
    }
  }

  fun removeWord(word: DictionaryWord) {
    viewModelScope.launch {
      repository.removeWord(word)
    }
  }

  fun resetWordProgress(word: DictionaryWord) {
    viewModelScope.launch {
      repository.resetWordProgress(word)
    }
  }
}
