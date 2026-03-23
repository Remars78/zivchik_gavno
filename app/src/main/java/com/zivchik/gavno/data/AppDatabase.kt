package com.zivchik.gavno.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [ConfigEntity::class], version = 1, exportSchema = false)
@TypeConverters(ConfigConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun configDao(): ConfigDao
}

// Converters for Enum or JSON objects if needed
import androidx.room.TypeConverter

class ConfigConverters {
    @TypeConverter
    fun fromType(value: ConfigType): String = value.name

    @TypeConverter
    fun toType(value: String): ConfigType = ConfigType.valueOf(value)
}
