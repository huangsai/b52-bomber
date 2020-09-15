package com.mobile.app.bomber.runner.features

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.mobile.guava.android.mvvm.AndroidX

internal fun Context.intentOf(className: String): Intent {
    return Intent().also {
        it.setClassName(this, className)
    }
}

internal fun newFragment(className: String): Fragment {
    return FragmentFactory().instantiate(AndroidX.myApp.classLoader, className)
}

inline fun <reified T> Activity.asApi(): T {
    return this as T
}

inline fun <reified T> Fragment.asApi(): T {
    return this as T
}