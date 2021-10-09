package ru.wherexibucks.database

import androidx.room.ColumnInfo
import androidx.room.Entity

/**
 * Связка много-много для соотношения символов и их составляющих
 * @property parent - символ
 * @property radical - составляющая
 */
@Entity(primaryKeys = ["parent", "radical"])
data class RadicalLink(
    @field:ColumnInfo(name = "parent") var parent: String,
    @field:ColumnInfo(name = "radical") var radical: String
)