package com.mobile.app.bomber.tik

import androidx.multidex.MultiDexApplication
import com.mobile.app.bomber.runner.RunnerX

class TikApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        RunnerX.setup(this, BuildConfig.DEBUG)
    }
}