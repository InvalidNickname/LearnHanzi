package ru.wherexibucks.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

/**
 * Объект доступа к базе данных
 */
@Dao
interface Dao {
    /**
     * Возвращает символы, которые можно начать учить - они еще не рассматривались до этого,
     * не имеют составляющих, либо все их составляющие полностью выучены
     */
    @Query("SELECT * FROM Card WHERE level = -1 AND (SELECT COUNT(*) FROM RadicalLink WHERE parent = symbol) = (SELECT COUNT(*) FROM RadicalLink WHERE parent = symbol AND (SELECT COUNT(*) FROM Card WHERE symbol = radical AND level = 5)>0)")
    suspend fun getReadyToBeLearnt(): Array<Card>

    /**
     * Возвращает количество символов, готовых для изучения
     */
    @Query("SELECT COUNT(*) FROM Card WHERE level = -1 AND (SELECT COUNT(*) FROM RadicalLink WHERE parent = symbol) = (SELECT COUNT(*) FROM RadicalLink WHERE parent = symbol AND (SELECT COUNT(*) FROM Card WHERE symbol = radical AND level = 5)>0)")
    suspend fun getReadyToBeLearntCount(): Int

    /**
     * Возвращает количество карточек, готовых для повторения
     */
    @Query("SELECT COUNT(*) FROM Card WHERE level >= 0 AND level < 9 AND time_till_next <= :time")
    suspend fun getReviewCount(time: Long): Int

    /**
     * Возвращает набор карточек, готовых для повторения
     */
    @Query("SELECT * FROM Card WHERE level >= 0 AND level < 9 AND time_till_next <= :time")
    suspend fun getReview(time: Long): Array<Card>

    /**
     * Возвращает количество карточек, выученных полностью
     */
    @Query("SELECT COUNT(*) FROM Card WHERE level = 9")
    suspend fun getFullyLearntCount(): Int

    /**
     * Обновление карточек
     * @param cards - карточки для обновления
     */
    @Update
    suspend fun updateCards(vararg cards: Card)

    /**
     * Добавляет все карточки в базу
     * @param cards - карточки для добавления
     */
    @Insert
    suspend fun insertAll(vararg cards: Card)

    /**
     * Возвращает количество карточек в базе
     */
    @Query("SELECT COUNT(*) FROM Card")
    suspend fun countAll(): Int
}