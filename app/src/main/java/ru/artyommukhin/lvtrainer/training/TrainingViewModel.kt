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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import ru.artyommukhin.lvtrainer.database.AppDatabase
import ru.artyommukhin.lvtrainer.dictionary.data.DictionaryWord

sealed interface TrainingUiState {
    data object Loading : TrainingUiState
    data object NoWords : TrainingUiState
    data object NoUntrainedWords : TrainingUiState
    class Active(
        val word: DictionaryWord,
        val isAnsweredCorrectly: Boolean? = null,
    ) : TrainingUiState {

        val isAnswered: Boolean = isAnsweredCorrectly != null
    }
}

@HiltViewModel(assistedFactory = TrainingViewModel.Factory::class)
class TrainingViewModel @AssistedInject constructor(
    database: AppDatabase,
    @Assisted val trainingType: TrainingType,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(trainingType: TrainingType): TrainingViewModel
    }

    private val dictionary = database.dictionaryWordsDao()
    private val _state = MutableStateFlow<TrainingUiState>(TrainingUiState.Loading)
    val state = _state.asStateFlow()

    init {
        init()
    }

    private fun init() {
        viewModelScope.launch(Dispatchers.IO) {
            val wordCount = dictionary.getCount()

            if (wordCount == 0) {
                _state.update { TrainingUiState.NoWords }
                return@launch
            }

            var word = dictionary.getRandomUntrainedWord()

            if (word == null) {
                _state.update { TrainingUiState.NoUntrainedWords }
                return@launch
            }

            if (wordCount == 1) {
                _state.update { TrainingUiState.Active(word) }
                return@launch
            }

            val currentWord = (_state.value as? TrainingUiState.Active)?.word

            while (word == currentWord) word = dictionary.getRandomUntrainedWord()

            _state.update { TrainingUiState.Active(word!!) }
        }
    }

    fun answer(word: DictionaryWord, userInput: String) {
        val correctAnswer = when (trainingType) {
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
            dictionary.increaseTrainCount(word.id)
            val newWord = dictionary.getById(word.id)!!

            _state.update {
                TrainingUiState.Active(newWord, true)
            }
        }
    }

    fun restart() {
        init()
    }
}