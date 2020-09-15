package com.mobile.app.bomber.data.db

import androidx.room.Dao
import androidx.room.Query
import com.mobile.app.bomber.data.db.entities.DbTikSearchKey
import com.mobile.guava.https.SqlDao
import kotlinx.coroutines.flow.Flow

@Dao
interface TikSearchKeyDao : SqlDao<DbTikSearchKey> {

    @Query("SELECT * FROM tik_search_key ORDER BY time DESC")
    fun get(): Flow<List<DbTikSearchKey>>

    @Query("SELECT * FROM tik_search_key WHERE name =:name")
    fun get(name: String): DbTikSearchKey?

    @Query("DELETE FROM tik_search_key")
    fun clear(): Int
}