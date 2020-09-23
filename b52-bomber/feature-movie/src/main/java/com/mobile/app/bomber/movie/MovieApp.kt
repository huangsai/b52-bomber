package com.mobile.app.bomber.movie

import androidx.multidex.MultiDexApplication
import com.mobile.app.bomber.runner.RunnerX

class MovieApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        RunnerX.setup(this, BuildConfig.DEBUG)
    }
}