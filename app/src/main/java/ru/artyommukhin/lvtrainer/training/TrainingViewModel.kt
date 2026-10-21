package ru.artyommukhin.lvtrainer.training

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.artyommukhin.lvtrainer.dictionary.data.DictionaryWord
import ru.artyommukhin.lvtrainer.dictionary.data.DictionaryWordsRepository

sealed interface TrainingUiState {
  data object Loading : TrainingUiState

  data object NoWords : TrainingUiState

  data object NoUntrainedWords : TrainingUiState

  data class Active(
      val word: DictionaryWord,
      val isAnsweredCorrectly: Boolean? = null,
  ) : TrainingUiState {

    val isAnswered: Boolean = isAnsweredCorrectly != null
  }
}

@HiltViewModel(assistedFactory = TrainingViewModel.Factory::class)
class TrainingViewModel
@AssistedInject
constructor(
    private val repository: DictionaryWordsRepository,
    @Assisted val trainingType: TrainingType,
) : ViewModel() {

  @AssistedFactory
  interface Factory {
    fun create(trainingType: TrainingType): TrainingViewModel
  }

  private val _state = MutableStateFlow<TrainingUiState>(TrainingUiState.Loading)
  val state = _state.asStateFlow()

  init {
    init()
  }

  private fun init() {
    viewModelScope.launch(Dispatchers.IO) {
      val words = repository.words.first()

      if (words.isEmpty) {
        _state.update { TrainingUiState.NoWords }
        return@launch
      }

      if (words.untrainedWords.isEmpty()) {
        _state.update { TrainingUiState.NoUntrainedWords }
        return@launch
      }

      if (words.untrainedWords.size == 1) {
        val word = words.untrainedWords[0]
        _state.update { TrainingUiState.Active(word) }
        return@launch
      }

      val currentWord = (_state.value as? TrainingUiState.Active)?.word

      var word: DictionaryWord
      do {
        word = words.untrainedWords.random()
      } while (word == currentWord)

      _state.update { TrainingUiState.Active(word) }
    }
  }

  fun answer(word: DictionaryWord, userInput: String) {
    val correctAnswer =
        when (trainingType) {
          TrainingType.WORD_TO_TRANSLATION -> word.translation == userInput.trim()
          TrainingType.TRANSLATION_TO_WORD -> word.word == userInput.trim()
        }

    if (!correctAnswer) {
      _state.update {
        TrainingUiState.Active(word, false)
      }
      return
    }

    viewModelScope.launch(Dispatchers.IO) {
      repository.increaseWordProgress(word)
      val newWord = repository.getWord(word.id)!!

      _state.update {
        TrainingUiState.Active(newWord, true)
      }
    }
  }

  fun restart() {
    init()
  }
}
