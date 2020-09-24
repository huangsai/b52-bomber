package com.mobile.app.bomber.tik.base

import android.util.Base64

@JvmOverloads
fun String.toBase64(flags: Int = Base64.NO_WRAP): String {
    return Base64.encodeToString(toByteArray(Charsets.UTF_8), flags)
}

@JvmOverloads
fun String.fromBase64(flags: Int = Base64.NO_WRAP): String {
    return Base64.decode(toByteArray(Charsets.UTF_8), flags).decodeToString()
}