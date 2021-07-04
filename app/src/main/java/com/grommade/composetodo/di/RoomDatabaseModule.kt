package com.grommade.composetodo.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.grommade.composetodo.data.AppDataBase
import com.grommade.composetodo.data.dao.SettingsDao
import com.grommade.composetodo.data.entity.Settings
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RoomDatabaseModule {

    @Provides
    @Singleton
    fun provideDataBase(@ApplicationContext context: Context, settingsDaoProvide: Provider<SettingsDao>): AppDataBase {
        return Room.databaseBuilder(
            context,
            AppDataBase::class.java,
            "lazy_to_do_2020"

        )
            .fallbackToDestructiveMigration()
            .addCallback(
                object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        CoroutineScope(SupervisorJob()).launch {
                            settingsDaoProvide.get().insert(Settings())
                        }
                    }

                    override fun onOpen(db: SupportSQLiteDatabase) {
                        super.onOpen(db)
                        CoroutineScope(SupervisorJob()).launch {
                            val settingsDao = settingsDaoProvide.get()
                            if (settingsDao.getCountSettings() == 0) {
                                settingsDao.insert(Settings())
                            }
                        }
                    }
                }
            )
            .build()
    }

}

@InstallIn(SingletonComponent::class)
@Module
object DatabaseDaoModule {
    @Provides
    fun provideSettingsDao(db: AppDataBase) = db.SettingsDao()

    @Provides
    fun provideTaskDao(db: AppDataBase) = db.SingleTaskDao()

    @Provides
    fun provideHistoryDao(db: AppDataBase) = db.HistoryDao()
}