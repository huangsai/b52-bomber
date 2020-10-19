package com.mobile.app.bomber.runner.features

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment

object FeatureRouter {

    private const val packageLive = "com.mobile.app.bomber.live."

    private const val packageMovie = "com.mobile.app.bomber.movie."

    private const val packageTik = "com.mobile.app.bomber.tik."

    private const val packageAuth = "com.mobile.app.bomber.auth."

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

    fun authActivityIntent(activity: Activity): Intent {
        return activity.intentOf(packageAuth + "AuthActivity")
    }

    fun startLoginActivity(activity: Activity): Intent {
        return activity.intentOf(packageTik + "login.LoginActivity")
    }
}