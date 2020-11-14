package com.mobile.app.bomber.common.base.tool

import android.app.Activity
import android.content.Intent

fun Activity.shareToSystem(data: String) {
    Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, data)
        type = "text/plain"
    }.also {
        startActivity(Intent.createChooser(it, null))
    }
}