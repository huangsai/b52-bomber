package com.mobile.app.bomber.data.repository

import com.mobile.app.bomber.data.db.AppDatabase
import com.mobile.app.bomber.data.files.AppPrefsManager
import com.mobile.app.bomber.data.http.entities.ApiVersion
import com.mobile.guava.https.toSource
import com.mobile.app.bomber.data.http.service.DataService
import com.mobile.guava.jvm.domain.Source
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VersionRepository @Inject constructor(
        dataService: DataService,
        db: AppDatabase,
        appPrefsManager: AppPrefsManager
) : BaseRepository(dataService, db, appPrefsManager) {


    suspend fun checkVersion(): Source<ApiVersion.Version> {
        return try {
            dataService.getVersion(1).execute().toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
    }
}