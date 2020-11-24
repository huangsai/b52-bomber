package com.mobile.app.bomber.common.base.tool

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.net.Uri
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import java.io.File

fun Activity.shareToSystem(data: String, Subtitle: String, url: File?) {
    Intent().apply {

//        action = Intent.ACTION_SEND
//        putExtra(Intent.EXTRA_TEXT, data)
//        type = "text/plain"
        if (url == null) {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_SUBJECT, Subtitle)
            putExtra(Intent.EXTRA_TEXT, Subtitle + data)
            type = "text/plain"
        } else {
            val uri = Uri.fromFile(url)
            val builder = VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
            builder.detectFileUriExposure()
            action = Intent.ACTION_SEND
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
            putExtra(Intent.EXTRA_STREAM, uri)
            type = "image/*"
        }
    }.also {
        startActivity(Intent.createChooser(it, null))
    }
}

fun Activity.isSoftInputShowing(): Boolean {
    //获取当屏幕内容的高度
    val screenHeight = this.window.decorView.height
    //获取View可见区域的bottom
    val rect = Rect()
    //DecorView即为activity的顶级view
    this.window.decorView.getWindowVisibleDisplayFrame(rect)
    //考虑到虚拟导航栏的情况（虚拟导航栏情况下：screenHeight = rect.bottom + 虚拟导航栏高度）
    //选取screenHeight*2/3进行判断
    return screenHeight * 2 / 3 > rect.bottom
}