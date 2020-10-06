package com.mobile.app.bomber.data.repository

import com.mobile.app.bomber.data.db.AppDatabase
import com.mobile.app.bomber.data.files.AppPrefsManager
import com.mobile.app.bomber.data.http.entities.ApiMovieBanner
import com.mobile.app.bomber.data.http.entities.Nope
import com.mobile.app.bomber.data.http.service.DataService
import com.mobile.guava.data.toSource
import com.mobile.guava.jvm.domain.Source
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
        dataService: DataService,
        db: AppDatabase,
        appPrefsManager: AppPrefsManager
) : BaseRepository(dataService, db, appPrefsManager) {

    suspend fun getBanner(): Source<List<ApiMovieBanner.Banner>> {
        val call = dataService.getBanner(0)
        return try {
            call.execute().toSource {
                it.banner.orEmpty()
            }
        } catch (e: Exception) {
            errorSource(e)
        }
    }
}