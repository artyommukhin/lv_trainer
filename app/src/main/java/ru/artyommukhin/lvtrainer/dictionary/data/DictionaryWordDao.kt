package ru.artyommukhin.lvtrainer.dictionary.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DictionaryWordDao {
    @Query("SELECT * FROM word")
    fun getAll(): List<DictionaryWord>

    @Query("SELECT * FROM word ORDER BY RANDOM() LIMIT 1")
    fun getRandomWord(): DictionaryWord?

    @Insert
    fun insert(vararg words: DictionaryWord)

    @Delete
    fun delete(vararg words: DictionaryWord)
}