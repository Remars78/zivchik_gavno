package com.zivchik.gavno.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ConfigDao {
    @Query("SELECT * FROM configs")
    fun getAllConfigs(): Flow<List<ConfigEntity>>

    @Query("SELECT * FROM configs WHERE type = :type")
    fun getConfigsByType(type: ConfigType): List<ConfigEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(config: ConfigEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfigs(configs: List<ConfigEntity>)

    @Query("DELETE FROM configs WHERE type = :type")
    suspend fun clearConfigsByType(type: ConfigType)

    @Update
    suspend fun updateConfig(config: ConfigEntity)
    
    @Query("DELETE FROM configs WHERE pingMs > 2000")
    suspend fun clearDeadConfigs()
}
