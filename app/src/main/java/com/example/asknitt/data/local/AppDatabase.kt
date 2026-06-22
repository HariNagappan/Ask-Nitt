package com.example.asknitt.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.asknitt.data.local.DAOs.AiChatDao
import com.example.asknitt.data.local.DAOs.AnswerDao
import com.example.asknitt.data.local.DAOs.DoubtDao
import com.example.asknitt.data.local.DAOs.SearchExploreDao
import com.example.asknitt.data.local.DAOs.SocialDao
import com.example.asknitt.data.local.DAOs.UserDao
import com.example.asknitt.data.local.Entities.AiChatEntity
import com.example.asknitt.data.local.Entities.AnswerEntity
import com.example.asknitt.data.local.Entities.DoubtEntity
import com.example.asknitt.data.local.Entities.ExploreCacheEntity
import com.example.asknitt.data.local.Entities.FriendEntity
import com.example.asknitt.data.local.Entities.FriendRequestEntity
import com.example.asknitt.data.local.Entities.SearchCacheEntity
import com.example.asknitt.data.local.Entities.UserEntity

@Database(
    entities = [
        DoubtEntity::class,
        UserEntity::class,
        FriendEntity::class,
        FriendRequestEntity::class,
        AnswerEntity::class,
        SearchCacheEntity::class,
        ExploreCacheEntity::class,
        AiChatEntity::class
    ],
    version = 3
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun doubtDao(): DoubtDao
    abstract fun userDao(): UserDao
    abstract fun socialDao(): SocialDao
    abstract fun answerDao(): AnswerDao
    abstract fun searchExploreDao(): SearchExploreDao
    abstract fun aiChatDao(): AiChatDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "Ask_Nitt_DB"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
