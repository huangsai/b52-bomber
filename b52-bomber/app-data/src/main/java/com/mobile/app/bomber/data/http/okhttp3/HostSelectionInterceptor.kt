package com.mobile.app.bomber.data.http.okhttp3

import com.mobile.app.bomber.data.DataX
import com.mobile.app.bomber.data.http.entities.*
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class HostSelectionInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val dynamicHttpUrl = createHttpUrl(request.url.toString())
        val newHttpUrl = request.url.newBuilder()
                .host(dynamicHttpUrl.host)
                .scheme(dynamicHttpUrl.scheme)
                .port(dynamicHttpUrl.port)
                .build()

        return chain.proceed(request.newBuilder().url(newHttpUrl).build())
    }

    private fun createHttpUrl(original: String): HttpUrl {
        val httpAddress = DataX.component.appPrefsManager().getHttpAddress()
        val httpAddressUpload = DataX.component.appPrefsManager().getHttpAddressUpload()
        return when (HOST_TAG) {
            HOST_TAG_TEST -> { //测试服
                when {
                    isAboutUpload(original) -> if (httpAddressUpload.isNotEmpty()) httpAddressUpload else HOST_TEST_UPLOAD
                    else -> if (httpAddress.isNotEmpty()) httpAddress else HOST_TEST
                }.toHttpUrl()
            }
            HOST_TAG_RELEASE -> { //正式服
                when {
                    isAboutUpload(original) -> if (httpAddressUpload.isNotEmpty()) httpAddressUpload else HOST_RELEASE_UPLOAD
                    else -> if (httpAddress.isNotEmpty()) httpAddress else HOST_RELEASE
                }.toHttpUrl()
            }
            else -> throw IllegalStateException()
        }
    }

    private fun isAboutUser(original: String): Boolean {
        if (original.contains("login")) return true
        if (original.contains("fastlogin")) return true
        if (original.contains("getverifycode")) return true
        if (original.contains("getuserinfo")) return true
        if (original.contains("modifyuserInfo")) return true
        if (original.contains("followone")) return true
        if (original.contains("changeheadpic")) return true
        if (original.contains("getfollowlist")) return true
        if (original.contains("getuserrank")) return true
        if (original.contains("getusercount")) return true
        if (original.contains("getfanslist")) return true
        if (original.contains("searchuser")) return true
        if (original.contains("isfollowed")) return true
        return false
    }

    private fun isAboutUpload(original: String): Boolean {
        if (original.contains("upload")) return true
        return false
    }

    private fun isAboutSystem(original: String): Boolean {
        if (original.contains("getindexmessage")) return true
        if (original.contains("getadconfig")) return true
        if (original.contains("getShareUrl")) return true
        return false
    }
}