package ru.wherexibucks.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Статистика по количеству проверок за день
 * @property date - дата
 * @property revRight - количество верных ответов за день
 * @property revWrong - количество неверных ответов за день
 */
@Entity
data class Stats(
    @field:ColumnInfo(name = "date") @PrimaryKey var date: String,
    @field:ColumnInfo(name = "rev_right") var revRight: Long,
    @field:ColumnInfo(name = "rev_wrong") var revWrong: Long
)