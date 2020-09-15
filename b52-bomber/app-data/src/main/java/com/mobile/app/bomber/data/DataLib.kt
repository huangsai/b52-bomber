package com.mobile.app.bomber.data

import com.mobile.app.bomber.data.db.AppDatabase
import com.mobile.app.bomber.data.files.AppPrefsManager
import com.mobile.guava.https.PlatformContext

object DataLib {

    @get:JvmName("component")
    lateinit var component: DataComponent
        private set

    fun setup(
            platformContext: PlatformContext,
            appDatabase: AppDatabase,
            appPrefsManager: AppPrefsManager
    ) {
        component = DaggerDataComponent.factory().create(
                platformContext,
                appDatabase,
                appPrefsManager
        )
    }
}