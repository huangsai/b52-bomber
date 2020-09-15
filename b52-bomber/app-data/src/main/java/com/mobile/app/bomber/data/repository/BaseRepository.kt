package com.mobile.app.bomber.data.repository

import com.mobile.app.bomber.data.db.AppDatabase
import com.mobile.app.bomber.data.files.AppPrefsManager
import com.mobile.app.bomber.data.http.service.DataService
import com.mobile.guava.jvm.Guava
import com.mobile.guava.jvm.domain.Source

abstract class BaseRepository(
        protected val dataService: DataService,
        protected val db: AppDatabase,
        protected val appPrefsManager: AppPrefsManager
) {

    protected val userId: Long
        get() = appPrefsManager.getUserId()

    protected val token: String
        get() = appPrefsManager.getToken()

    protected val orBlankToken: String
        get() = appPrefsManager.getToken().ifEmpty { "blank" }

    protected fun <T> errorSource(e: Throwable): Source<T> {
        Guava.timber.d(e)
        return Source.Error(e)
    }
}