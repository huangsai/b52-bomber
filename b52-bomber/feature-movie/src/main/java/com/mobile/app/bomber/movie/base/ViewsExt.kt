package com.mobile.app.bomber.movie.base

import android.app.Activity
import android.transition.TransitionManager
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.fragment.app.FragmentActivity
import com.mobile.app.bomber.movie.base.views.Rotate
import com.mobile.app.bomber.runner.base.PrefsManager
import com.mobile.app.bomber.runner.features.FeatureRouter

fun ImageView.animRotate(degrees: Float) {
    TransitionManager.beginDelayedTransition(this.parent as ViewGroup, Rotate())
    this.rotation = degrees
}

private val okResult by lazy { ActivityResult(Activity.RESULT_OK, null) }

fun Activity.requireLogin(callback: ActivityResultCallback<ActivityResult>) {
    if (PrefsManager.isLogin()) {
        callback.onActivityResult(okResult)
    } else {
        this.startActivity(FeatureRouter.startLoginActivity(this))
    }
}