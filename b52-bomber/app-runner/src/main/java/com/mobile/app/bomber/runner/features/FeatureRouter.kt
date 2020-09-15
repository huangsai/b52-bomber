package com.mobile.app.bomber.runner.features

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment

object FeatureRouter {

    private const val packageLive = "com.mobile.app.bomber.live."

    private const val packageMovie = "com.mobile.app.bomber.movie."

    private const val packageTik = "com.mobile.app.bomber.tik."

    /**
     * 把newInstant()方法搬来这里，把函数名字改一下即可
     */
    fun newMovieFragment(position: Int): Fragment {
        return newFragment(packageMovie + "MovieFragment").apply {
            arguments = Bundle().apply {
                putInt("position", position)
            }
        }
    }

    /**
     * 考虑到参数安全问题，最终还是决定在这里创建每一个接口Activity，把参数都补齐
     */
    fun newLiveActivity(context: Context): Intent {
        return context.intentOf(packageLive + "LiveActivity")
    }
}