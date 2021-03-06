package com.mobile.app.bomber.data.files

interface AppPrefsManager {

    fun getUserId(): Long

    fun setUserId(userId: Long): Boolean

    fun getDeviceId(): String

    fun getToken(): String

    fun setToken(token: String): Boolean

    fun getRefresh(): String

    fun setRefresh(token: String): Boolean

    fun getLoginName(): String

    fun setLoginName(loginName: String): Boolean

    fun getHeadPicUrl(): String

    fun setHeadPicUrl(headPicUrl: String): Boolean

    fun getLoginPassword(): String

    fun setLoginPassword(loginPassword: String): Boolean

    fun getLoginType(): Int

    fun setLoginType(loginType: Int): Boolean

    fun getSoftKeyboardHeight(): Int

    fun setSoftKeyboardHeight(loginType: Int): Boolean

    fun isLogin(): Boolean

    fun setIsLogin(isLogin: Boolean): Boolean

    fun getMsgTime(type: String): Int

    fun setMsgTime(time: Int, type: String): Boolean

    fun getHttpAddress(): String

    fun setHttpAddress(address: String): Boolean

    fun getHttpAddressUpload(): String

    fun setHttpAddressUpload(addressUpload: String): Boolean
}