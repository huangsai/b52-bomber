package com.mobile.app.bomber.movie.search

import androidx.annotation.WorkerThread
import com.mobile.app.bomber.common.base.MyBaseViewModel
import com.mobile.app.bomber.data.db.entities.DbMovieSearchKey
import com.mobile.guava.android.ensureWorkThread
import javax.inject.Inject

class SearchViewModel @Inject constructor() : MyBaseViewModel() {

    @WorkerThread
    suspend fun getKeys(): List<DbMovieSearchKey> {
        ensureWorkThread()
        return movieSearchRepository.getKeys()
    }

    @WorkerThread
    suspend fun getKey(key: String): DbMovieSearchKey? {
        ensureWorkThread()
        return movieSearchRepository.getKey(key)
    }

    @WorkerThread
    suspend fun addKey(key: String): DbMovieSearchKey {
        ensureWorkThread()
        return movieSearchRepository.addKey(key)
    }

    @WorkerThread
    suspend fun clearKeys(): Int {
        ensureWorkThread()
        return movieSearchRepository.clearKeys()
    }

    @WorkerThread
    fun deleteKey(obj: DbMovieSearchKey): Int {
        ensureWorkThread()
        return movieSearchRepository.deleteKey(obj)
    }
}