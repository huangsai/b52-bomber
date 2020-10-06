package com.mobile.app.bomber.data.db

import androidx.room.Dao
import androidx.room.Query
import com.mobile.app.bomber.data.db.entities.DbMovieSearchKey
import com.mobile.guava.data.SqlDao

@Dao
interface MovieSearchKeyDao : SqlDao<DbMovieSearchKey> {

    @Query("SELECT * FROM movie_search_key ORDER BY time DESC")
    suspend fun get(): List<DbMovieSearchKey>

    @Query("SELECT * FROM movie_search_key WHERE name =:name")
    suspend fun get(name: String): DbMovieSearchKey?

    @Query("DELETE FROM movie_search_key")
    suspend fun clear(): Int
}