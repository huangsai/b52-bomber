package com.mobile.app.bomber.common.base.tool

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.common.base.MyBaseActivity
import com.mobile.app.bomber.runner.base.PrefsManager
//import com.mobile.app.bomber.tik.login.LoginActivity

//private val okResult by lazy { ActivityResult(Activity.RESULT_OK, null) }

//fun FragmentActivity.requireLogin(callback: ActivityResultCallback<ActivityResult>) {
//    if (PrefsManager.isLogin()) {
//        callback.onActivityResult(okResult)
//    } else {
//        (this as MyBaseActivity)
//                .registerForActivityResult2(startActivityContract, callback)
//                .launch(Intent(this, LoginActivity::class.java))
//    }
//}

fun Activity.chrome(url: String?) {
    if (url.isNullOrEmpty()) {
        Msg.toast("链接地址为空")
        return
    }
    if (!url.contains(".")) {
        Msg.toast("无法用浏览器打开${url}")
        return
    }
    try {
        if (url.startsWith("http") || url.startsWith("https")) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        } else {
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://$url")))
            } catch (e: Exception) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("http://$url")))
            }
        }
    } catch (e: Exception) {
        Msg.toast("无法用浏览器打开${url}")
    }
}

fun Fragment.chrome(url: String?) = requireActivity().chrome(url)

@JvmOverloads
fun Activity.newStartActivityForResult(to: Class<*>, requestCode: Int, extras: Bundle? = null) {
    val intent = Intent()
    intent.setClass(this, to)
    if (extras != null) {
        intent.putExtras(extras)
    }
    this.startActivityForResult(intent, requestCode)
}