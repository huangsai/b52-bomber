package com.mobile.app.bomber.runner.base

import android.location.Location
import android.location.LocationManager
import com.mobile.app.bomber.data.files.AppPrefsManager
import com.mobile.app.bomber.runner.RunnerX
import com.mobile.guava.android.log.uniqueId
import com.mobile.guava.android.mvvm.AndroidX
import com.tencent.mmkv.MMKV
import timber.log.Timber
import java.util.*
import kotlin.time.measureTime

object PrefsManager : AppPrefsManager {

    private val mmvk: MMKV by lazy {
        MMKV.initialize(AndroidX.myApp)
        return@lazy MMKV.defaultMMKV()
    }

    override fun getUserId(): Long {
        return mmvk.decodeLong(RunnerX.PREFS_USER_ID, 0L)
    }

    override fun setUserId(userId: Long): Boolean {
        return mmvk.encode(RunnerX.PREFS_USER_ID, userId)
    }

    override fun getDeviceId(): String {
        var deviceId = mmvk.decodeString(RunnerX.PREFS_DEVICE_ID, "")
        if (deviceId.isEmpty()) {
            deviceId = try {
                uniqueId(AndroidX.myApp)
            } catch (ignored: Exception) {
                Timber.e(ignored)
                UUID.randomUUID().toString()
            }
            mmvk.encode(RunnerX.PREFS_DEVICE_ID, deviceId)
        }
        return deviceId
    }

    override fun getToken(): String {
        return mmvk.decodeString(RunnerX.PREFS_TOKEN, "")
    }

    override fun setToken(token: String): Boolean {
        return mmvk.encode(RunnerX.PREFS_TOKEN, token)
    }


    override fun getRefresh(): String {
        return mmvk.decodeString(RunnerX.BUS_FRAGMENT_ME_REFRESH_FIRST, "")
    }

    override fun setRefresh(token: String): Boolean {
        return mmvk.encode(RunnerX.BUS_FRAGMENT_ME_REFRESH_FIRST, token)
    }


    override fun getLoginName(): String {
        return mmvk.decodeString(RunnerX.PREFS_LOGIN_NAME, "")
    }

    override fun setLoginName(loginName: String): Boolean {
        return mmvk.encode(RunnerX.PREFS_LOGIN_NAME, loginName)
    }

    override fun getHeadPicUrl(): String {
        return mmvk.decodeString(RunnerX.PREFS_HEAD_PIC_URL, "")
    }

    override fun setHeadPicUrl(headPicUrl: String): Boolean {
        return mmvk.encode(RunnerX.PREFS_HEAD_PIC_URL, headPicUrl)
    }

    override fun getLoginPassword(): String {
        return mmvk.decodeString(RunnerX.PREFS_LOGIN_PASSWORD, "")
    }

    override fun setLoginPassword(loginPassword: String): Boolean {
        return mmvk.encode(RunnerX.PREFS_LOGIN_PASSWORD, loginPassword)
    }

    override fun getLoginType(): Int {
        return mmvk.decodeInt(RunnerX.PREFS_LOGIN_TYPE, 1)
    }

    override fun setLoginType(loginType: Int): Boolean {
        return mmvk.encode(RunnerX.PREFS_LOGIN_TYPE, loginType)
    }

    override fun getSoftKeyboardHeight(): Int {
        return mmvk.decodeInt(RunnerX.PREFS_SOFT_HEIGHT, 0)
    }

    override fun setSoftKeyboardHeight(height: Int): Boolean {
        return mmvk.encode(RunnerX.PREFS_SOFT_HEIGHT, height)
    }

    override fun isLogin(): Boolean {
        return mmvk.decodeBool(RunnerX.PREFS_IS_LOGIN, false)
    }

    override fun setIsLogin(isLogin: Boolean): Boolean {
        return mmvk.encode(RunnerX.PREFS_IS_LOGIN, isLogin)
    }

    fun getLocation(): Location? {
        val decodeLatitude = mmvk.decodeDouble(RunnerX.PREFS_LOCATION_LAT)
        val decodeLongitude = mmvk.decodeDouble(RunnerX.PREFS_LOCATION_LNG)
        if (decodeLatitude < 1) return null
        val location = Location(LocationManager.GPS_PROVIDER)
        location.latitude = decodeLatitude
        location.longitude = decodeLongitude
        return location
    }

    fun setLocation(location: Location) {
        mmvk.encode(RunnerX.PREFS_LOCATION_LAT, location.latitude)
        mmvk.encode(RunnerX.PREFS_LOCATION_LNG, location.longitude)
    }

    override fun getMsgTime(type: String): Int {
        return mmvk.decodeInt(getUserId().toString() + type, 0)
    }

    override fun setMsgTime(time: Int, type: String): Boolean {
        return mmvk.encode(getUserId().toString() + type, time)
    }

    override fun getHttpAddress(): String {
        return mmvk.decodeString(RunnerX.PREFS_HTTP_ADDRESS, "")
    }

    override fun setHttpAddress(address: String): Boolean {
        return mmvk.encode(RunnerX.PREFS_HTTP_ADDRESS, address)
    }

    override fun getHttpAddressUpload(): String {
        return mmvk.decodeString(RunnerX.PREFS_HTTP_ADDRESS_UPLOAD, "")
    }

    override fun setHttpAddressUpload(addressUpload: String): Boolean {
        return mmvk.encode(RunnerX.PREFS_HTTP_ADDRESS_UPLOAD, addressUpload)

    }
}