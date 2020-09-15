package com.mobile.app.bomber.common.base.tool

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.view.WindowManager

fun Activity.requestFullScreenWithLandscape() {
    with(window) {
        val wmLayoutParams = this.attributes
        wmLayoutParams.flags = wmLayoutParams.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
        attributes = wmLayoutParams
        addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }
    if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }
}

fun Activity.requestNormalScreenWithPortrait() {
    with(window) {
        val wmLayoutParams = this.attributes
        wmLayoutParams.flags = wmLayoutParams.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
        attributes = wmLayoutParams
        clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
    }
    if (requestedOrientation != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
}

fun Activity.isLandscape(): Boolean = requestedOrientation == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

fun Activity.shareToSystem(data: String) {
    Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, data)
        type = "text/plain"
    }.also {
        startActivity(Intent.createChooser(it, null))
    }
}