package com.mobile.app.bomber.data.repository

import com.mobile.app.bomber.data.db.AppDatabase
import com.mobile.app.bomber.data.db.entities.DbTikSearchKey
import com.mobile.app.bomber.data.files.AppPrefsManager
import com.mobile.app.bomber.data.http.entities.ApiAtUser
import com.mobile.app.bomber.data.http.entities.ApiVideo
import com.mobile.app.bomber.data.http.entities.Pager
import com.mobile.app.bomber.data.http.service.DataService
import com.mobile.guava.data.toSource
import com.mobile.guava.jvm.domain.Source
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import sun.rmi.runtime.Log
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TikSearchRepository @Inject constructor(
        dataService: DataService,
        db: AppDatabase,
        appPrefsManager: AppPrefsManager
) : BaseRepository(dataService, db, appPrefsManager) {

    fun getKeys(): Flow<List<DbTikSearchKey>> {
        val dao = db.tikSearchKeyDao()
        return dao.get()
    }

    fun addKey(key: String): DbTikSearchKey {
        val dao = db.tikSearchKeyDao()
        val dbSearchKey = dao.get(key)
        val obj: DbTikSearchKey
        if (dbSearchKey == null) {
            dao.insert(DbTikSearchKey(0, key, System.currentTimeMillis()).also {
                obj = it
            })
        } else {
            dao.update(dbSearchKey.copy(time = System.currentTimeMillis()).also {
                obj = it
            })
        }
        return obj
    }

    fun clearKeys(): Int {
        val dao = db.tikSearchKeyDao()
        return dao.clear()
    }

    fun deleteKey(obj: DbTikSearchKey): Int {
        val dao = db.tikSearchKeyDao()
        return dao.delete(obj)
    }

    suspend fun getHotKeyTopN(): Source<List<String>> {
        val call = dataService.getHotKey()
        return try {
            call.execute().toSource() {
                it.KeyWords
            }
        } catch (e: Exception) {
            errorSource<Exception>(e)
        } as Source<List<String>>
    }

    suspend fun searchTikVideoList(keyword: String, pager: Pager): Source<List<ApiVideo.Video>> {
        if (pager.isReachedTheEnd) return Source.Success(emptyList())
        pager.isBusy = true
        val call = dataService.searchVideo(userId, keyword, pager.requestPage, pager.pageSize, Any())
        return callApiVideo(call, pager)
    }


    suspend fun searchTikUserList(keyword: String): Source<List<ApiAtUser.User>> {
        var uids: Long = 0L
        if (!appPrefsManager.isLogin()) {
            uids = 0L
        } else {
            uids = appPrefsManager.getUserId()
        }
        val call = dataService.searchTikUsers(keyword, uids)
        return try {
            call.execute().toSource() {
                it.users
            }
        } catch (e: Exception) {
            errorSource<Exception>(e)
        } as Source<List<ApiAtUser.User>>
    }

    private suspend fun callApiVideo(call: Call<ApiVideo>, pager: Pager): Source<List<ApiVideo.Video>> {
        pager.isBusy = true
        return try {
            call.execute().toSource {
                pager.nextPage(it.totalPage)
                it.videos.orEmpty()
            }
        } catch (e: Exception) {
            pager.isBusy = false
            errorSource(e)
        }
    }
}