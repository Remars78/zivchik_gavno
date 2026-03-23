package com.zivchik.gavno.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "configs")
data class ConfigEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: ConfigType,
    val name: String,
    val rawJson: String, // Sing-box outbound configuration as JSON string
    val pingMs: Long = -1,
    val isLastWorking: Boolean = false,
    val lastUpdateTime: Long = System.currentTimeMillis()
)

enum class ConfigType {
    VLESS,
    AWG,
    WHITELIST
}
