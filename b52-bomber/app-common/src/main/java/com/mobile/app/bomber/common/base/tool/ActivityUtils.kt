package com.mobile.app.bomber.common.base.tool

import android.app.Activity
import android.content.Intent
import android.graphics.Rect

fun Activity.shareToSystem(data: String) {
    Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, data)
        type = "text/plain"
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