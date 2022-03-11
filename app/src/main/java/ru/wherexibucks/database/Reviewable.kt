package ru.wherexibucks.database

import androidx.room.ColumnInfo
import androidx.room.Entity

@Entity
open class Reviewable(
    @field:ColumnInfo(name = "pinyin") open var pinyin: String,
    @field:ColumnInfo(name = "definition") open var definition: String,
    @field:ColumnInfo(name = "memo") open var memo: String,
    @field:ColumnInfo(name = "level") open var level: Int,
    @field:ColumnInfo(name = "time_till_next") open var time: Long
)