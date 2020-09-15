package com.mobile.app.bomber.tik

import androidx.multidex.MultiDexApplication
import com.mobile.app.bomber.runner.RunnerLib

class TikApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        RunnerLib.setup(this, BuildConfig.DEBUG)
    }
}