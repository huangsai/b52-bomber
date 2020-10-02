package com.mobile.app.bomber.tik.base

import android.app.Activity
import android.util.Log
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.mobile.app.bomber.data.http.entities.DECODE_URL
import com.bumptech.glide.request.RequestOptions
import com.mobile.ext.glide.GlideApp
import com.mobile.app.bomber.tik.R

private val sharedProfileRequestOptions by lazy {
    RequestOptions()
            .placeholder(R.drawable.default_profile)
            .centerCrop()
}

fun Activity.loadProfile(url: String?, imageView: ImageView) {
    GlideApp.with(this).load((url))
            .apply(sharedProfileRequestOptions)
            .thumbnail(0.25f)
            .into(imageView)
}

fun Fragment.loadProfile(url: String?, imageView: ImageView) {
    GlideApp.with(this).load((url))
            .apply(sharedProfileRequestOptions)
            .thumbnail(0.25f)
            .into(imageView)
}

fun decodeImgUrl(url: String?): String {
    return url.toString()

//    return "${DECODE_URL}/videodecode?videourl=${url}&download=0"
}


