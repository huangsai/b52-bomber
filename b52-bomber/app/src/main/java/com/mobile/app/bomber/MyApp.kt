package com.mobile.app.bomber

import android.util.Log
import androidx.multidex.MultiDexApplication
import com.flurry.android.FlurryAgent
import com.flurry.android.FlurryPerformance
import com.mobile.app.bomber.runner.RunnerX
import com.mobile.sdk.ipv6.Ipv6X


class MyApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        RunnerX.setup(this, BuildConfig.DEBUG)

        Ipv6X.setup(this,true)
        FlurryAgent.Builder()
                .withDataSaleOptOut(true) //CCPA - the default value is false
                .withCaptureUncaughtExceptions(true)
                .withIncludeBackgroundSessionsInMetrics(true)
                .withLogLevel(Log.VERBOSE)
                .withPerformanceMetrics(FlurryPerformance.ALL)
                .build(this, "BM6DYJYMWNFT6TNJCPMB")
    }
}