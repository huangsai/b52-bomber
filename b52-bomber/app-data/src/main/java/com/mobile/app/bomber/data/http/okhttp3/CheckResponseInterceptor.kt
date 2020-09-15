package com.mobile.app.bomber.data.http.okhttp3

import com.mobile.app.bomber.data.DataLib
import com.mobile.app.bomber.data.http.entities.Nope
import com.mobile.app.bomber.data.repository.toSourceException
import com.mobile.guava.jvm.Guava
import com.mobile.guava.jvm.domain.PlatformTimber
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class CheckResponseInterceptor : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (Guava.timber != PlatformTimber.SYSTEM) {
            var json = response.peekBody(Long.MAX_VALUE).string()
            if (json.contains("retcode")) {
                json = json.replace("retcode", "RetCode")
            }
            val code = DataLib.component.json().adapter(Nope::class.java).fromJson(json)!!.code
            if (code != 0) {
                when (code) {
                    5 -> {
                        DataLib.component.appPrefsManager().setIsLogin(false)
                        DataLib.component.appPrefsManager().setToken("")
                        DataLib.component.appPrefsManager().setUserId(0L)
                    }
                }
                throw code.toSourceException()
            }
        }
        return response
    }
}
