package com.mobile.app.bomber.movie.search

import androidx.annotation.WorkerThread
import com.mobile.app.bomber.common.base.MyBaseViewModel
import com.mobile.app.bomber.data.db.entities.DbMovieSearchKey
import com.mobile.app.bomber.data.http.entities.ApiMovie
import com.mobile.app.bomber.data.http.entities.ApiMovieHotKey
import com.mobile.app.bomber.data.http.entities.Nope
import com.mobile.app.bomber.data.http.entities.Pager
import com.mobile.guava.android.ensureWorkThread
import com.mobile.guava.jvm.domain.Source
import javax.inject.Inject

class SearchViewModel @Inject constructor() : MyBaseViewModel() {

    @WorkerThread
    suspend fun getHotKeys(): Source<List<ApiMovieHotKey.MovieHotKey>> {
        ensureWorkThread()
        return movieSearchRepository.getHotKeys()
    }

    @WorkerThread
    suspend fun searchMovie(keyword: String, pager: Pager): Source<List<ApiMovie.Movie>> {
        ensureWorkThread()
        return movieSearchRepository.searchMovie(keyword, pager)
    }

    @WorkerThread
    suspend fun playSearchMovie(mid: Int): Source<Nope> {
        ensureWorkThread()
        return movieSearchRepository.playSearchMovie(mid)
    }

    @WorkerThread
    suspend fun getKeys(): List<DbMovieSearchKey> {
        ensureWorkThread()
        return movieSearchRepository.getKeys()
    }

//    @WorkerThread
//    suspend fun getKey(key: String): DbMovieSearchKey? {
//        ensureWorkThread()
//        return movieSearchRepository.getKey(key)
//    }

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

//    @WorkerThread
//    fun deleteKey(obj: DbMovieSearchKey): Int {
//        ensureWorkThread()
//        return movieSearchRepository.deleteKey(obj)
//    }
}