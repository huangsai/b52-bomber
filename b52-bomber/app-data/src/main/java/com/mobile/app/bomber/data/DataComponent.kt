package com.mobile.app.bomber.data

import com.mobile.app.bomber.data.db.AppDatabase
import com.mobile.app.bomber.data.files.AppPrefsManager
import com.mobile.app.bomber.data.http.service.DataService
import com.mobile.app.bomber.data.repository.*
import com.mobile.guava.https.PlatformContext
import com.squareup.moshi.Moshi
import dagger.BindsInstance
import dagger.Component
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

@Component(modules = [DataModule::class])
@Singleton
interface DataComponent {

    fun poorX509TrustManager(): X509TrustManager

    fun poorSSLContext(): SSLContext

    fun httpLoggingInterceptorLogger(): HttpLoggingInterceptor.Logger

    fun httpLoggingInterceptor(): HttpLoggingInterceptor

    fun okHttpClient(): OkHttpClient

    fun json(): Moshi

    fun retrofit(): Retrofit

    fun dataService(): DataService

    fun videoRepository(): VideoRepository

    fun userRepository(): UserRepository

    fun adRepository(): AdRepository

    fun uploadRepository(): UploadRepository

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