package com.mobile.app.bomber.data

import com.mobile.app.bomber.data.http.okhttp3.CheckResponseInterceptor
import com.mobile.app.bomber.data.http.okhttp3.HostSelectionInterceptor
import com.mobile.app.bomber.data.http.service.DataService
import com.mobile.guava.data.SimpleDataModule
import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.LoggingEventListener
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit
import javax.inject.Singleton
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

@Module
class DataModule : com.mobile.guava.data.DataModule {

    private val delegate = SimpleDataModule()

    @Provides
    @Singleton
    override fun providePoorX509TrustManager(): X509TrustManager {
        return delegate.providePoorX509TrustManager()
    }

    @Provides
    @Singleton
    override fun providePoorSSLContext(x509TrustManager: X509TrustManager): SSLContext {
        return delegate.providePoorSSLContext(x509TrustManager)
    }

    @Provides
    @Singleton
    override fun provideHttpLoggingInterceptorLogger(): HttpLoggingInterceptor.Logger {
        return delegate.provideHttpLoggingInterceptorLogger()
    }

    @Provides
    @Singleton
    override fun provideHttpLoggingInterceptor(
            httpLoggingInterceptorLogger: HttpLoggingInterceptor.Logger
    ): HttpLoggingInterceptor {
        return delegate.provideHttpLoggingInterceptor(httpLoggingInterceptorLogger)
    }

    @Provides
    @Singleton
    override fun provideOkHttpClient(
            x509TrustManager: X509TrustManager,
            sslContext: SSLContext,
            httpLoggingInterceptor: HttpLoggingInterceptor,
            httpLoggingInterceptorLogger: HttpLoggingInterceptor.Logger
    ): OkHttpClient {
        return OkHttpClient().newBuilder()
                .addInterceptor(HostSelectionInterceptor())
                .addInterceptor(CheckResponseInterceptor())
                .addInterceptor(httpLoggingInterceptor)
                .eventListenerFactory(LoggingEventListener.Factory(httpLoggingInterceptorLogger))
                .sslSocketFactory(sslContext.socketFactory, x509TrustManager)
                .hostnameVerifier(HostnameVerifier { _, _ -> true })
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(50, TimeUnit.SECONDS)
                .writeTimeout(50, TimeUnit.SECONDS)
                .build()
    }

    @Provides
    @Singleton
    override fun provideJson(): Moshi {
        return delegate.provideJson()
    }

    @Provides
    @Singleton
    override fun provideRetrofit(okHttpClient: OkHttpClient, moshi: Moshi): Retrofit {
        return delegate.provideRetrofit(okHttpClient, moshi)
    }

    @Provides
    @Singleton
    fun provideDataService(retrofit: Retrofit): DataService {
        return retrofit.create(DataService::class.java)
    }
}