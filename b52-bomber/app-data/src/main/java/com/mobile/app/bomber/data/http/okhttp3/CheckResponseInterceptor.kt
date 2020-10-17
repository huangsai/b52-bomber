package com.mobile.app.bomber.data.http.okhttp3

import com.mobile.app.bomber.data.DataX
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
            when {
                json.contains("retcode") -> {
                    json = json.replace("retcode", "retCode")
                }
                json.contains("Retcode") -> {
                    json = json.replace("Retcode", "retCode")
                }
                json.contains("RetCode") -> {
                    json = json.replace("RetCode", "retCode")
                }
            }
            val code = DataX.component.json()
                    .adapter(Nope::class.java)
                    .lenient()
                    .fromJson(json)!!.code
            if (code != 0) {
                when (code) {
                    5 -> {
                        DataX.component.appPrefsManager().setIsLogin(false)
                        DataX.component.appPrefsManager().setToken("")
                        DataX.component.appPrefsManager().setUserId(0L)
                    }
                }
                throw code.toSourceException()
            }
        }
        return response
    }
}
