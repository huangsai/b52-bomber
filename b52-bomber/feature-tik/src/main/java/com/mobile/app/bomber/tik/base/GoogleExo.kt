package com.mobile.app.bomber.tik.base

import android.net.Uri
import com.google.android.exoplayer2.DefaultControlDispatcher
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory
import com.google.android.exoplayer2.upstream.cache.CacheUtil
import com.google.android.exoplayer2.upstream.cache.LeastRecentlyUsedCacheEvictor
import com.google.android.exoplayer2.upstream.cache.SimpleCache
import com.google.android.exoplayer2.util.Util
import com.mobile.guava.https.nullSafe
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.guava.https.createPoorSSLOkHttpClient
import com.mobile.guava.jvm.Guava
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.io.File

object GoogleExo : CacheUtil.ProgressListener {

    private val cache by lazy {
        SimpleCache(
                File(AndroidX.myApp.externalCacheDir, "video_manager_disk_cache"),
                LeastRecentlyUsedCacheEvictor(250 * 1024 * 1024),
                ExoDatabaseProvider(AndroidX.myApp)
        )
    }

    private val upstreamFactory by lazy {
        OkHttpDataSourceFactory(
                createPoorSSLOkHttpClient("ExoPlayer"),
                Util.getUserAgent(AndroidX.myApp, "com.mobile.app.bomber.tik")
        )
    }

    val cacheDataSourceFactory = CacheDataSourceFactory(cache, upstreamFactory)
    val controlDispatcher = DefaultControlDispatcher()

    fun preload(uri: Uri) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val nKB = if (Guava.isDebug) 64L else 1024L
                val dataSpec = DataSpec(uri, 0L, nKB * 1024L, null)
                val cached = CacheUtil.getCached(dataSpec, cache, null)
                if (cached.second >= cached.first) {
                    Timber.tag("ExoPlayer").d("skip preload")
                } else {
                    CacheUtil.cache(
                            dataSpec,
                            cache,
                            upstreamFactory.createDataSource(),
                            this@GoogleExo,
                            null
                    )
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