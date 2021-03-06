package com.mobile.app.bomber.data.repository

import com.mobile.app.bomber.data.db.AppDatabase
import com.mobile.app.bomber.data.files.AppPrefsManager
import com.mobile.app.bomber.data.http.entities.ApiDownLoadUrl
import com.mobile.app.bomber.data.http.entities.ApiShareUrl
import com.mobile.app.bomber.data.http.service.DataService
import com.mobile.guava.data.toSource
import com.mobile.guava.jvm.domain.Source
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ShareRepository @Inject constructor(
        dataService: DataService,
        db: AppDatabase,
        appPrefsManager: AppPrefsManager
) : BaseRepository(dataService, db, appPrefsManager) {

    suspend fun getShareUrl(): Source<ApiShareUrl> {
        return try {
            dataService.getShareUrl().toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun getDownLoadUrl(): Source<ApiDownLoadUrl> {
        return try {
            dataService.getDownLoadUrl().toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
    }
}