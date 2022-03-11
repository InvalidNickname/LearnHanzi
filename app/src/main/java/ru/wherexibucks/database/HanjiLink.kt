package ru.wherexibucks.database

import androidx.room.ColumnInfo
import androidx.room.Entity

/**
 * Связка много-много для соотношения слов и их составляющих
 * @property word - символ
 * @property hanji - составляющая
 */
@Entity(primaryKeys = ["word", "hanji"])
data class HanjiLink(
    @field:ColumnInfo(name = "word") var word: String,
    @field:ColumnInfo(name = "hanji") var hanji: String
)