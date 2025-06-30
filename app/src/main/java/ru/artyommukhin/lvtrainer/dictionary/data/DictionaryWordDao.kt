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
    suspend fun getCount(): Int

    @Query("SELECT * FROM word WHERE id = :wordId")
    suspend fun getById(wordId: Int): DictionaryWord?

    @Query("SELECT * FROM word WHERE trainCount < $TRAINED_THRESHOLD ORDER BY RANDOM() LIMIT 1")
    suspend fun getRandomUntrainedWord(): DictionaryWord?

    @Query("UPDATE word SET trainCount = trainCount + 1 WHERE id = :wordId")
    suspend fun increaseTrainCount(wordId: Int)

    @Query("UPDATE word SET trainCount = 0 WHERE id = :wordId")
    suspend fun resetTrainCount(wordId: Int)

    @Insert
    suspend fun insert(vararg words: DictionaryWord)

    @Delete
    suspend fun delete(vararg words: DictionaryWord)

    companion object {
        const val TRAINED_THRESHOLD = 5
    }
}