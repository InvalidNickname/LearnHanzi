package ru.wherexibucks.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Класс данных карточки со словами
 * @property word - само слово
 * @property pinyin - пиньинь
 * @property definition - определение, ключевое слово
 * @property memo - подсказка для запоминания
 * @property level - уровень изучения, -1 - еще не рассматривался, 5 - полностью выучен
 * @property time - время следующего повторения
 */
@Entity
data class Word(
    @field:ColumnInfo(name = "word") @PrimaryKey var word: String,
    @field:ColumnInfo(name = "pinyin") override var pinyin: String,
    @field:ColumnInfo(name = "definition") override var definition: String,
    @field:ColumnInfo(name = "memo") override var memo: String,
    @field:ColumnInfo(name = "level") override var level: Int,
    @field:ColumnInfo(name = "time_till_next") override var time: Long
) : Reviewable(pinyin, definition, memo, level, time)