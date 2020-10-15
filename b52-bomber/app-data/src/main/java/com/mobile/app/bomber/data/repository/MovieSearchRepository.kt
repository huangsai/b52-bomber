package com.mobile.app.bomber.data.repository

import com.mobile.app.bomber.data.db.AppDatabase
import com.mobile.app.bomber.data.db.entities.DbMovieSearchKey
import com.mobile.app.bomber.data.files.AppPrefsManager
import com.mobile.app.bomber.data.http.entities.*
import com.mobile.app.bomber.data.http.service.DataService
import com.mobile.guava.data.toSource
import com.mobile.guava.jvm.domain.Source
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieSearchRepository @Inject constructor(
        dataService: DataService,
        db: AppDatabase,
        appPrefsManager: AppPrefsManager
) : BaseRepository(dataService, db, appPrefsManager) {

    suspend fun getHotKeys(): Source<List<ApiMovieHotKey.MovieHotKey>> {
        val call = dataService.getMovieHotKey()
        return try {
            call.execute().toSource {
                it.hotKeys.orEmpty()
            }
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun searchMovie(keyword: String, pager: Pager): Source<List<ApiMovie.Movie>> {
        if (pager.isReachedTheEnd) return Source.Success(emptyList())
        pager.isBusy = true
        val call = dataService.searchMovie(ApiMovie.ReqSearch(keyword, pager.requestPage, pager.pageSize))
        return try {
            call.execute().toSource {
                pager.nextPage(it.totalPage)
                it.movies.orEmpty()
            }
        } catch (e: Exception) {
            pager.isBusy = false
            errorSource(e)
        }
    }

    suspend fun playSearchMovie(mid: Int): Source<Nope> {
        val call = dataService.playSearchMovie(mid)
        return try {
            call.execute().toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun getKeys(): List<DbMovieSearchKey> {
        val dao = db.movieSearchKeyDao()
        return dao.get()
    }

    suspend fun getKey(key: String): DbMovieSearchKey? {
        val dao = db.movieSearchKeyDao()
        return dao.get(key)
    }

    suspend fun addKey(key: String): DbMovieSearchKey {
        val dao = db.movieSearchKeyDao()
        val dbMovieSearchKey = dao.get(key)
        val obj: DbMovieSearchKey
        if (dbMovieSearchKey == null) {
            dao.insert(DbMovieSearchKey(0, key, System.currentTimeMillis()).also {
                obj = it
            })
        } else {
            dao.update(dbMovieSearchKey.copy(time = System.currentTimeMillis()).also {
                obj = it
            })
        }
        return obj
    }

    suspend fun clearKeys(): Int {
        val dao = db.movieSearchKeyDao()
        return dao.clear()
    }

    fun deleteKey(obj: DbMovieSearchKey): Int {
        val dao = db.movieSearchKeyDao()
        return dao.delete(obj)
    }
}