package com.mobile.app.bomber.tik

import androidx.multidex.MultiDexApplication
import com.mobile.app.bomber.runner.RunnerX
import com.mobile.sdk.ipv6.Ipv6X

class TikApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        RunnerX.setup(this, BuildConfig.DEBUG)
        Ipv6X.setup(this,true)
    }
}