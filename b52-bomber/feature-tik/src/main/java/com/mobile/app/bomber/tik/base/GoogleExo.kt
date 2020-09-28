package com.mobile.app.bomber.tik.base

import android.content.Context
import android.net.Uri
import android.text.TextUtils
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultControlDispatcher
import com.google.android.exoplayer2.ExoPlayerLibraryInfo
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.database.ExoDatabaseProvider
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.upstream.*
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

    fun buildMediaSource(context: Context, uri: Uri, supportPreview: Boolean): MediaSource {

        var streamType = Util.inferContentType(uri.lastPathSegment ?: "")
        if (streamType == C.TYPE_OTHER) {
            streamType = Util.inferContentType(uri)
        }
        val mediaItem = MediaItem.Builder()
                .setUri(uri)
                .setTag(uri.toString())
                .build()

        val bandwidthMeter: DefaultBandwidthMeter? = if (supportPreview) {
            null
        } else {
            DefaultBandwidthMeter.Builder(context).build()
        }
        return when (streamType) {
            C.TYPE_DASH -> DashMediaSource.Factory(buildDataSourceFactory(context, null))
                    .createMediaSource(mediaItem)
            C.TYPE_SS -> SsMediaSource.Factory(buildDataSourceFactory(context, null))
                    .createMediaSource(mediaItem)
            C.TYPE_HLS -> HlsMediaSource.Factory(buildDataSourceFactory(context, bandwidthMeter))
                    .createMediaSource(mediaItem)
            C.TYPE_OTHER -> ProgressiveMediaSource.Factory(buildDataSourceFactory(context, bandwidthMeter))
                    .createMediaSource(mediaItem)
            else -> throw UnsupportedOperationException("Unknown stream type")
        }
    }

    private fun buildDataSourceFactory(
            context: Context,
            bandwidthMeter: DefaultBandwidthMeter?
    ): DataSource.Factory {
        return DefaultDataSourceFactory(
                context,
                bandwidthMeter,
                cacheDataSourceFactory
        )
    }
}