package com.mobile.app.bomber.movie

import androidx.multidex.MultiDexApplication
import com.mobile.app.bomber.runner.RunnerLib

class MovieApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        RunnerLib.setup(this, BuildConfig.DEBUG)
    }
}