package ru.artyommukhin.lvtrainer.dictionary.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word")
data class DictionaryWord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val word: String,
    val translation: String,
    @ColumnInfo(defaultValue = "0") val trainCount: Int = 0,
    @ColumnInfo(defaultValue = NO_LANGUAGE_TAG) val wordLanguageTag: String,
    @ColumnInfo(defaultValue = NO_LANGUAGE_TAG) val translationLanguageTag: String,
) {
  companion object {
    const val NO_LANGUAGE_TAG = "NO_LANGUAGE_TAG"
  }
}
