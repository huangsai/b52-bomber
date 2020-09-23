package com.mobile.app.bomber

import androidx.multidex.MultiDexApplication
import com.mobile.app.bomber.runner.RunnerX

class MyApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        RunnerX.setup(this, BuildConfig.DEBUG)
    }
}