package com.mobile.app.bomber.runner.base

import android.location.Location
import android.location.LocationManager
import com.mobile.app.bomber.data.files.AppPrefsManager
import com.mobile.app.bomber.runner.RunnerLib
import com.mobile.guava.android.log.uniqueId
import com.mobile.guava.android.mvvm.AndroidX
import com.tencent.mmkv.MMKV
import timber.log.Timber
import java.util.*

object PrefsManager : AppPrefsManager {

    private val mmvk: MMKV by lazy {
        MMKV.initialize(AndroidX.myApp)
        return@lazy MMKV.defaultMMKV()
    }

    override fun getUserId(): Long {
        return mmvk.decodeLong(RunnerLib.PREFS_USER_ID, 0L)
    }

    override fun setUserId(userId: Long): Boolean {
        return mmvk.encode(RunnerLib.PREFS_USER_ID, userId)
    }

    override fun getDeviceId(): String {
        var deviceId = mmvk.decodeString(RunnerLib.PREFS_DEVICE_ID, "")
        if (deviceId.isEmpty()) {
            deviceId = try {
                uniqueId(AndroidX.myApp)
            } catch (ignored: Exception) {
                Timber.e(ignored)
                UUID.randomUUID().toString()
            }
            mmvk.encode(RunnerLib.PREFS_DEVICE_ID, deviceId)
        }
        return deviceId
    }

    override fun getToken(): String {
        return mmvk.decodeString(RunnerLib.PREFS_TOKEN, "")
    }

    override fun setToken(token: String): Boolean {
        return mmvk.encode(RunnerLib.PREFS_TOKEN, token)
    }

    override fun getLoginName(): String {
        return mmvk.decodeString(RunnerLib.PREFS_LOGIN_NAME, "")
    }

    override fun setLoginName(loginName: String): Boolean {
        return mmvk.encode(RunnerLib.PREFS_LOGIN_NAME, loginName)
    }

    override fun getHeadPicUrl(): String {
        return mmvk.decodeString(RunnerLib.PREFS_HEAD_PIC_URL, "")
    }

    override fun setHeadPicUrl(headPicUrl: String): Boolean {
        return mmvk.encode(RunnerLib.PREFS_HEAD_PIC_URL, headPicUrl)
    }

    override fun getLoginPassword(): String {
        return mmvk.decodeString(RunnerLib.PREFS_LOGIN_PASSWORD, "")
    }

    override fun setLoginPassword(loginPassword: String): Boolean {
        return mmvk.encode(RunnerLib.PREFS_LOGIN_PASSWORD, loginPassword)
    }

    override fun getLoginType(): Int {
        return mmvk.decodeInt(RunnerLib.PREFS_LOGIN_TYPE, 1)
    }

    override fun setLoginType(loginType: Int): Boolean {
        return mmvk.encode(RunnerLib.PREFS_LOGIN_TYPE, loginType)
    }

    override fun getSoftKeyboardHeight(): Int {
        return mmvk.decodeInt(RunnerLib.PREFS_SOFT_HEIGHT, 0)
    }

    override fun setSoftKeyboardHeight(height: Int): Boolean {
        return mmvk.encode(RunnerLib.PREFS_SOFT_HEIGHT, height)
    }

    override fun isLogin(): Boolean {
        return mmvk.decodeBool(RunnerLib.PREFS_IS_LOGIN, false)
    }

    override fun setIsLogin(isLogin: Boolean): Boolean {
        return mmvk.encode(RunnerLib.PREFS_IS_LOGIN, isLogin)
    }

    fun getLocation(): Location? {
        val decodeLatitude = mmvk.decodeDouble(RunnerLib.PREFS_LOCATION_LAT)
        val decodeLongitude = mmvk.decodeDouble(RunnerLib.PREFS_LOCATION_LNG)
        if (decodeLatitude < 1) return null
        val location = Location(LocationManager.GPS_PROVIDER)
        location.latitude = decodeLatitude
        location.longitude = decodeLongitude
        return location
    }

    fun setLocation(location: Location) {
        mmvk.encode(RunnerLib.PREFS_LOCATION_LAT, location.latitude)
        mmvk.encode(RunnerLib.PREFS_LOCATION_LNG, location.longitude)
    }
}