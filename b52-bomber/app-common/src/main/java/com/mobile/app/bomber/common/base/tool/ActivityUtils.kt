package com.mobile.app.bomber.common.base.tool

import android.app.Activity
import android.content.Intent
import com.mobile.guava.android.mvvm.AndroidX

val realScreen by lazy {
    AppUtil.getRealSize(AndroidX.myApp)
}

fun Activity.shareToSystem(data: String) {
    Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, data)
        type = "text/plain"
    }.also {
        startActivity(Intent.createChooser(it, null))
    }
}