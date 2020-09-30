package com.mobile.app.bomber.movie.player.exo

import android.content.res.Resources
import android.net.Uri
import com.google.android.exoplayer2.C
import com.google.android.exoplayer2.trackselection.MappingTrackSelector
import com.mobile.app.bomber.movie.R
import okio.ByteString.Companion.encodeUtf8

const val TAG_EXO_PLAYER = "ExoPlayer"

fun isSupportedRenderer(
    mappedTrackInfo: MappingTrackSelector.MappedTrackInfo,
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

fun Uri.md5Key():String = this.toString().encodeUtf8().md5().hex()

fun String.md5Key():String = this.encodeUtf8().md5().hex()