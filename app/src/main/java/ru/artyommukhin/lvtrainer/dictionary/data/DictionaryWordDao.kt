package ru.artyommukhin.lvtrainer.dictionary.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DictionaryWordDao {

    @Query("SELECT * FROM word")
    fun getAll(): List<DictionaryWord>

    @Query("SELECT COUNT(*) FROM word")
    fun getCount(): Int

    @Query("SELECT * FROM word WHERE id = :wordId")
    fun getById(wordId: Int): DictionaryWord?

    @Query("SELECT * FROM word WHERE trainCount < 5 ORDER BY RANDOM() LIMIT 1")
    fun getRandomUntrainedWord(): DictionaryWord?

    @Query("UPDATE word SET trainCount = trainCount + 1 WHERE id = :wordId")
    fun increaseTrainCount(wordId: Int)

    @Query("UPDATE word SET trainCount = 0 WHERE id = :wordId")
    fun resetTrainCount(wordId: Int)

    @Insert
    fun insert(vararg words: DictionaryWord)

    @Delete
    fun delete(vararg words: DictionaryWord)
}