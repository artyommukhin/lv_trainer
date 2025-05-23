package ru.artyommukhin.lvtrainer.dictionary.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "word")
data class DictionaryWord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo val word: String,
    @ColumnInfo val translation: String,
)