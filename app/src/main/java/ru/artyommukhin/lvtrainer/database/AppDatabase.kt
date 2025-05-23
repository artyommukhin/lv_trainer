package ru.artyommukhin.lvtrainer.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.artyommukhin.lvtrainer.dictionary.data.DictionaryWordDao
import ru.artyommukhin.lvtrainer.dictionary.data.DictionaryWord

@Database(entities = [DictionaryWord::class], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun dictionaryWordsDao(): DictionaryWordDao
}