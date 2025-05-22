package ru.artyommukhin.lvtrainer.dictionary

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class DictionaryState(
    val words: List<DictionaryWord> = listOf(
        DictionaryWord("abols", "яблоко"),
        DictionaryWord("durvis", "дверь"),
        DictionaryWord("logs", "окно"),
        DictionaryWord("abols", "яблоко"),
        DictionaryWord("durvis", "дверь"),
        DictionaryWord("logs", "окно"),
    ),
)

class DictionaryViewModel : ViewModel() {
    private val _state = MutableStateFlow(DictionaryState())
    val state = _state.asStateFlow()

    fun addWord(word: DictionaryWord) {
        _state.update { it.copy(words = it.words + word) }
    }

    fun removeWordAt(index: Int) {
        _state.update {
            val newWords = it.words.toMutableList().apply { removeAt(index) }
            it.copy(words = newWords)
        }
    }
}