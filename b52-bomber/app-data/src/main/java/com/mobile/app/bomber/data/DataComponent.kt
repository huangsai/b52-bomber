package com.mobile.app.bomber.data

import com.mobile.app.bomber.data.db.AppDatabase
import com.mobile.app.bomber.data.files.AppPrefsManager
import com.mobile.app.bomber.data.http.service.DataService
import com.mobile.app.bomber.data.repository.*
import com.mobile.guava.https.HttpsComponent
import com.mobile.guava.https.PlatformContext
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Component(modules = [DataModule::class])
@Singleton
interface DataComponent : HttpsComponent {

    fun dataService(): DataService

    fun videoRepository(): VideoRepository

    fun userRepository(): UserRepository

    fun versionRepository(): VersionRepository

    fun adRepository(): AdRepository

    fun uploadRepository(): UploadRepository

    fun shareRepository(): ShareRepository

    fun msgRepository(): MsgRepository

    fun tikSearchRepository(): TikSearchRepository

    fun movieSearchRepository(): MovieSearchRepository

    fun commentRepository(): CommentRepository

    fun appDatabase(): AppDatabase

    fun appPrefsManager(): AppPrefsManager

    @Component.Factory
    interface Factory {

        fun create(
                @BindsInstance platformContext: PlatformContext,
                @BindsInstance appDatabase: AppDatabase,
                @BindsInstance appPrefsManager: AppPrefsManager
        ): DataComponent
    }
}