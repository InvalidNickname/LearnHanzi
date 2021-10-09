package ru.wherexibucks.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Класс данных карточки с ханьцзы
 * @property symbol - сам символ
 * @property pinyin - пиньинь
 * @property definition - определение, ключевое слово
 * @property memo - подсказка для запоминания
 * @property level - уровень изучения, -1 - еще не рассматривался, 5 - полностью выучен
 * @property time - время следующего повторения
 */
@Entity
data class Card(
    @field:ColumnInfo(name = "symbol") @PrimaryKey var symbol: String,
    @field:ColumnInfo(name = "pinyin") var pinyin: String,
    @field:ColumnInfo(name = "definition") var definition: String,
    @field:ColumnInfo(name = "memo") var memo: String,
    @field:ColumnInfo(name = "level") var level: Int,
    @field:ColumnInfo(name = "time_till_next") var time: Long
)