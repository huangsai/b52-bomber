package com.mobile.app.bomber.data.repository

import com.mobile.app.bomber.data.db.AppDatabase
import com.mobile.app.bomber.data.files.AppPrefsManager
import com.mobile.app.bomber.data.http.entities.*
import com.mobile.guava.data.toSource
import com.mobile.app.bomber.data.http.service.DataService

import com.mobile.guava.jvm.domain.Source
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MsgRepository @Inject constructor(
        dataService: DataService,
        db: AppDatabase,
        appPrefsManager: AppPrefsManager
) : BaseRepository(dataService, db, appPrefsManager) {

    suspend fun likeList(pager: Pager): Source<List<ApiLikeList.Item>> {
        if (!appPrefsManager.isLogin()) {
            return Source.Error(sourceException403)
        }
        if (pager.isReachedTheEnd) return Source.Success(emptyList())
        pager.isBusy = true
        val call = dataService.likeList(userId, token, pager.requestPage, pager.pageSize)
        return try {
            call.execute().toSource {
                pager.nextPage(it.totalPage)
                it.items.orEmpty()
            }
        } catch (e: Exception) {
            pager.isBusy = false
            errorSource(e)
        }
    }

    suspend fun atList(pager: Pager): Source<List<ApiAtList.Item>> {
        if (!appPrefsManager.isLogin()) {
            return Source.Error(sourceException403)
        }
        if (pager.isReachedTheEnd) return Source.Success(emptyList())
        pager.isBusy = true
        val call = dataService.atList(userId, token, pager.requestPage, pager.pageSize)
        return try {
            call.execute().toSource {
                pager.nextPage(it.totalPage)
                it.items.orEmpty()
            }
        } catch (e: Exception) {
            pager.isBusy = false
            errorSource(e)
        }
    }

    suspend fun commentList(pager: Pager): Source<List<ApiCommentList.Item>> {
        if (!appPrefsManager.isLogin()) {
            return Source.Error(sourceException403)
        }
        if (pager.isReachedTheEnd) return Source.Success(emptyList())
        pager.isBusy = true
        val call = dataService.commentList(
                userId, token, pager.requestPage, pager.pageSize
        )
        return try {
            call.execute().toSource {
                pager.nextPage(it.totalPage)
                it.items.orEmpty()
            }
        } catch (e: Exception) {
            pager.isBusy = false
            errorSource(e)
        }
    }
}





