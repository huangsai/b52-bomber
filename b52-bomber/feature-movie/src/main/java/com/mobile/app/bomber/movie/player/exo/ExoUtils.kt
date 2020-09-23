package com.mobile.app.bomber.movie.player.exo

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import androidx.core.net.toUri
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.DefaultControlDispatcher
import com.google.android.exoplayer2.ExoPlayerLibraryInfo
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ext.okhttp.OkHttpDataSourceFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.source.smoothstreaming.SsMediaSource
import com.google.android.exoplayer2.trackselection.MappingTrackSelector.MappedTrackInfo
import com.google.android.exoplayer2.upstream.*
import com.google.android.exoplayer2.util.Util
import com.mobile.app.bomber.movie.R
import com.mobile.guava.https.createPoorSSLOkHttpClient
import okhttp3.OkHttpClient

object ExoUtils {

    const val TAG = "ExoPlayer"

    val controlDispatcher = DefaultControlDispatcher()

    val exoPlayerOkHttpClient: OkHttpClient by lazy {
        createPoorSSLOkHttpClient(TAG)
    }

    fun isSupportedRenderer(
            mappedTrackInfo: MappedTrackInfo,
            rendererIndex: Int
    ): Boolean {
        val trackGroupArray = mappedTrackInfo.getTrackGroups(rendererIndex)
        if (trackGroupArray.length == 0) {
            return false
        }
        val trackType = mappedTrackInfo.getRendererType(rendererIndex)
        return isSupportedTrackType(trackType)
    }

    fun isSupportedTrackType(trackType: Int): Boolean {
        return when (trackType) {
            C.TRACK_TYPE_VIDEO, C.TRACK_TYPE_AUDIO, C.TRACK_TYPE_TEXT -> true
            else -> false
        }
    }

    fun getTrackTypeString(resources: Resources, trackType: Int): String {
        return when (trackType) {
            C.TRACK_TYPE_VIDEO -> resources.getString(R.string.exo_track_selection_title_video)
            C.TRACK_TYPE_AUDIO -> resources.getString(R.string.exo_track_selection_title_audio)
            C.TRACK_TYPE_TEXT -> resources.getString(R.string.exo_track_selection_title_text)
            else -> throw IllegalArgumentException()
        }
    }

    fun buildMediaSource(context: Context, uri: Uri, supportPreview: Boolean): MediaSource {
        var streamType = Util.inferContentType(uri.lastPathSegment!!)
        if (streamType == C.TYPE_OTHER) {
            streamType = Util.inferContentType(uri)
        }
        val mediaItem = MediaItem.Builder()
                .setUri(uri)
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
                buildHttpDataSourceFactory(bandwidthMeter)
        )
    }

    private fun buildHttpDataSourceFactory(
            bandwidthMeter: DefaultBandwidthMeter?
    ): HttpDataSource.Factory {
        return OkHttpDataSourceFactory(
                exoPlayerOkHttpClient,
                ExoPlayerLibraryInfo.DEFAULT_USER_AGENT,
                bandwidthMeter
        )
    }
}