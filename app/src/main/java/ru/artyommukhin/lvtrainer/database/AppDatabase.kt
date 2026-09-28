package ru.artyommukhin.lvtrainer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.artyommukhin.lvtrainer.dictionary.data.DictionaryWord
import ru.artyommukhin.lvtrainer.dictionary.data.DictionaryWordDao

@Database(entities = [DictionaryWord::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
  abstract fun dictionaryWordsDao(): DictionaryWordDao
}
