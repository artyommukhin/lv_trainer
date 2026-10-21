package ru.artyommukhin.lvtrainer.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import ru.artyommukhin.lvtrainer.dictionary.data.DictionaryWord
import ru.artyommukhin.lvtrainer.dictionary.data.DictionaryWordDao

@Database(
    entities = [DictionaryWord::class],
    version = 2,
    autoMigrations =
        [
            AutoMigration(from = 1, to = 2),
        ],
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun dictionaryWordsDao(): DictionaryWordDao
}
