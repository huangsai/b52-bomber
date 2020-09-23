package com.mobile.app.bomber.runner

import android.app.Application
import android.os.StrictMode
import androidx.room.Room
import com.mobile.app.bomber.data.DataX
import com.mobile.app.bomber.runner.base.PrefsManager
import com.mobile.app.bomber.runner.base.RoomAppDatabase
import com.mobile.app.bomber.runner.dagger.DaggerRunnerComponent
import com.mobile.app.bomber.runner.dagger.RunnerComponent
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.guava.android.mvvm.AppContext
import com.mobile.guava.android.mvvm.AppManager

object RunnerX {

    const val PREFS_DEVICE_ID = "deviceId"
    const val PREFS_USER_ID = "userId"
    const val PREFS_TOKEN = "token"
    const val PREFS_LOGIN_NAME = "loginName"
    const val PREFS_HEAD_PIC_URL = "headPicUrl"
    const val PREFS_LOGIN_PASSWORD = "loginPassword"
    const val PREFS_LOGIN_TYPE = "loginType"
    const val PREFS_SOFT_HEIGHT = "softHeight"
    const val PREFS_IS_LOGIN = "isLogin"
    const val PREFS_LOCATION_LAT = "locationLat"
    const val PREFS_LOCATION_LNG = "locationLng"

    const val BUS_FRAGMENT_ME_REFRESH = 100
    const val BUS_SEARCH_RESULT = 102
    const val BUS_VIDEO_UPDATE = 103

    lateinit var component: RunnerComponent
        private set

    fun setup(app: Application, isDebug: Boolean) {
        AndroidX.setup(app, isDebug)

        DataX.setup(AppContext(), createRoomDatabase(), PrefsManager)

        component = DaggerRunnerComponent.factory().create(DataX.component, app)

        AppManager.initialize()

        if (isDebug) {
            enableStrictMode()
        }
    }

    private fun createRoomDatabase(): RoomAppDatabase {
        return Room.databaseBuilder(
                AndroidX.myApp, RoomAppDatabase::class.java, "app_bomber.db3")
                .addCallback(RoomAppDatabase.DbCallback())
                .addMigrations()
                .build()
    }

    private fun enableStrictMode() {
        StrictMode.setThreadPolicy(
                StrictMode.ThreadPolicy.Builder()
                        .detectAll()
                        .penaltyLog()
                        .build()
        )
        StrictMode.setVmPolicy(
                StrictMode.VmPolicy.Builder()
                        .detectAll()
                        .penaltyLog()
                        .build()
        )
    }
}






