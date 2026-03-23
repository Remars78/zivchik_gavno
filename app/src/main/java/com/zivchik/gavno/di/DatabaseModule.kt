package com.zivchik.gavno.di

import android.content.Context
import androidx.room.Room
import com.zivchik.gavno.data.AppDatabase
import com.zivchik.gavno.data.ConfigDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "zivchik_db"
        ).build()
    }

    @Provides
    fun provideConfigDao(db: AppDatabase): ConfigDao {
        return db.configDao()
    }
}
