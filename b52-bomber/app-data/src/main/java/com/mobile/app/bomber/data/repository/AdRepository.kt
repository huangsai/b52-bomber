package com.mobile.app.bomber.data.repository

import com.mobile.app.bomber.data.db.AppDatabase
import com.mobile.app.bomber.data.files.AppPrefsManager
import com.mobile.app.bomber.data.http.entities.ApiAd
import com.mobile.app.bomber.data.http.entities.ApiAdMsg
import com.mobile.app.bomber.data.http.service.DataService
import com.mobile.guava.https.toSource
import com.mobile.guava.jvm.domain.Source
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdRepository @Inject constructor(
        dataService: DataService,
        db: AppDatabase,
        appPrefsManager: AppPrefsManager
) : BaseRepository(dataService, db, appPrefsManager) {

    suspend fun adMsg(type:Int): Source<ApiAdMsg> {
        return try {
            dataService.adMsg(type).execute().toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun ad(type:Int): Source<ApiAd> {
        return try {
            dataService.ad(type).execute().toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
    }
}