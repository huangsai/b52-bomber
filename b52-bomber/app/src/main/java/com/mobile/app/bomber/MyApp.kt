package com.mobile.app.bomber

import android.util.Log
import androidx.multidex.MultiDexApplication
import com.flurry.android.FlurryAgent
import com.flurry.android.FlurryPerformance
import com.mobile.app.bomber.runner.RunnerX


class MyApp : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        RunnerX.setup(this, BuildConfig.DEBUG)
//        Ipv6X.setup(this,true)
        FlurryAgent.Builder()
                .withLogEnabled(true)
                .build(this, "BM6DYJYMWNFT6TNJCPMB");
//        FlurryAgent.Builder()
//                .withDataSaleOptOut(false) //CCPA - the default value is false
//                .withCaptureUncaughtExceptions(true)
//                .withIncludeBackgroundSessionsInMetrics(true)
//                .withLogLevel(Log.VERBOSE)
//                .withPerformanceMetrics(FlurryPerformance.ALL)
//                .build(this, FLURRY_API_KEY)
    }
}