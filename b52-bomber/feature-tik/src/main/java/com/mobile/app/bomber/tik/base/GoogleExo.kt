package com.mobile.app.bomber.tik.base

import android.net.Uri
import com.google.android.exoplayer2.DefaultControlDispatcher
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.cache.CacheDataSource
import com.google.android.exoplayer2.upstream.cache.CacheWriter
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.guava.https.createPoorSSLOkHttpClient
import com.mobile.guava.https.nullSafe
import com.mobile.guava.jvm.Guava
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

object GoogleExo : CacheWriter.ProgressListener {

    private val cache by lazy {
        SimpleCache(
                File(AndroidX.myApp.externalCacheDir, "video_manager_disk_cache"),
                LeastRecentlyUsedCacheEvictor(250 * 1024 * 1024),
                ExoDatabaseProvider(AndroidX.myApp)
        )
    }

    val cacheDataSourceFactory = CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(OkHttpDataSourceFactory(
                    createPoorSSLOkHttpClient("ExoPlayer"),
                    Util.getUserAgent(AndroidX.myApp, "com.mobile.app.bomber.tik")
            ))

    val controlDispatcher = DefaultControlDispatcher()

    fun preload(uri: Uri) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val requestCacheLength = (if (Guava.isDebug) 64L else 1024L) * 1024L
                val dataSpec = DataSpec.Builder()
                        .setUri(uri)
                        .setPosition(0L)
                        .setLength(requestCacheLength)
                        .setKey(uri.toString())
                        .build()
                val cached = cache.getCachedBytes(uri.toString(), 0, requestCacheLength)
                if (cached > 0) {
                    Timber.tag("ExoPlayer").d("skip preload")
                } else {
                    CacheWriter(
                            cacheDataSourceFactory.createDataSource(),
                            dataSpec,
                            true,
                            null,
                            this@GoogleExo

                    ).cache()
                }
            } catch (e: Exception) {
                Timber.tag("ExoPlayer").d(e.message.nullSafe())
            }
        }
    }

    override fun onProgress(requestLength: Long, bytesCached: Long, newBytesCached: Long) {
        Timber.tag("ExoPlayer").d(
                "requestLength %s -bytesCached %s -newBytesCached %s",
                requestLength,
                bytesCached,
                newBytesCached
        )
    }
}