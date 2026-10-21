package ru.artyommukhin.lvtrainer.dictionary.data

import java.util.Locale
import javax.inject.Inject
import javax.inject.Named
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import ru.artyommukhin.lvtrainer.database.AppDatabase
import ru.artyommukhin.lvtrainer.di.LanguageModule

class DictionaryWordsRepository
@Inject
constructor(
    database: AppDatabase,
    @Named(LanguageModule.DEFAULT_LOCALE) private val defaultLocale: Locale,
) {
  private val dictionary = database.dictionaryWordsDao()
  private val defaultLanguageTag: String = defaultLocale.toLanguageTag()

  val words =
      combine(
          dictionary.getAllUntrained().handleDefaultLanguageTags(),
          dictionary.getAllTrained().handleDefaultLanguageTags(),
      ) { untrained, trained ->
        WordsResult(untrained, trained)
      }

  suspend fun getWord(id: Int): DictionaryWord? {
    val word = dictionary.getById(id)?.run(::handleWordDefaultLanguageTags)
    return word
  }

  suspend fun addWord(word: DictionaryWord) {
    dictionary.insert(word)
  }

  suspend fun updateWord(word: DictionaryWord) {
    dictionary.update(word)
  }

  suspend fun removeWord(word: DictionaryWord) {
    dictionary.delete(word)
  }

  suspend fun increaseWordProgress(word: DictionaryWord) {
    dictionary.increaseTrainCount(word.id)
  }

  suspend fun resetWordProgress(word: DictionaryWord) {
    dictionary.resetTrainCount(word.id)
  }

  private fun Flow<List<DictionaryWord>>.handleDefaultLanguageTags() = map { words ->
    words.map(::handleWordDefaultLanguageTags)
  }

  private fun handleWordDefaultLanguageTags(word: DictionaryWord): DictionaryWord {
    val isDefaultWord = word.wordLanguageTag == DictionaryWord.NO_LANGUAGE_TAG
    val isDefaultTranslation = word.translationLanguageTag == DictionaryWord.NO_LANGUAGE_TAG

    if (!isDefaultWord && !isDefaultTranslation) return word

    return word.copy(
        wordLanguageTag = if (isDefaultWord) defaultLanguageTag else word.wordLanguageTag,
        translationLanguageTag =
            if (isDefaultTranslation) defaultLanguageTag else word.translationLanguageTag,
    )
  }

  class WordsResult(
      val untrainedWords: List<DictionaryWord>,
      val trainedWords: List<DictionaryWord>,
  ) {
    val isEmpty = untrainedWords.isEmpty() && trainedWords.isEmpty()
  }
}
