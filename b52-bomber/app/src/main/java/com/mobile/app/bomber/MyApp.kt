package com.mobile.app.bomber

import androidx.multidex.MultiDexApplication
import com.mobile.app.bomber.runner.RunnerLib

class MyApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        RunnerLib.setup(this, BuildConfig.DEBUG)
    }
}