package ru.wherexibucks.database

import androidx.room.Database
import androidx.room.RoomDatabase

/**
 * Класс базы данных
 */
@Database(entities = [Card::class, RadicalLink::class, Stats::class, Word::class, HanjiLink::class], version = 2, exportSchema = false)
abstract class Database : RoomDatabase() {
    abstract fun dao(): Dao?
}