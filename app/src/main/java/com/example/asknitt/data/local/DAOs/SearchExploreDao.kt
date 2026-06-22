package com.example.asknitt.data.local.DAOs

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.asknitt.data.local.Entities.ExploreCacheEntity
import com.example.asknitt.data.local.Entities.SearchCacheEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchExploreDao {
    @Query("SELECT * FROM search_results")
    fun getSearchCache(): Flow<List<SearchCacheEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchResults(results: List<SearchCacheEntity>)

    @Query("DELETE FROM search_results")
    suspend fun clearSearchCache()

    @Query("SELECT * FROM explore_users")
    fun getExploreCache(): Flow<List<ExploreCacheEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExploreUsers(users: List<ExploreCacheEntity>)

    @Query("DELETE FROM explore_users")
    suspend fun clearExploreCache()
}
