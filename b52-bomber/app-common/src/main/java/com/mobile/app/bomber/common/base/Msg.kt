package com.mobile.app.bomber.common.base

import android.widget.Toast
import androidx.annotation.StringRes
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.guava.jvm.domain.SourceException

object Msg {

    fun toast(msg: String) {
        Toast.makeText(AndroidX.myApp, msg, Toast.LENGTH_SHORT).show()
    }

    fun toast(@StringRes msg: Int) {
        Toast.makeText(AndroidX.myApp, msg, Toast.LENGTH_SHORT).show()
    }

    fun handleSourceException(e: Throwable) {
        if (e is SourceException) {
            when (e.code) {
                1, 2, 3, 5, 6, 11, 12, 14 -> toast(e.message.orEmpty())
                else -> toast("操作失败")
            }
        } else
            toast("网络异常")
    }
}