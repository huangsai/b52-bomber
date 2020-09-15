package com.mobile.app.bomber.movie.base

import android.transition.TransitionManager
import android.view.ViewGroup
import android.widget.ImageView
import com.mobile.app.bomber.movie.base.views.Rotate

fun ImageView.animRotate(degrees: Float) {
    TransitionManager.beginDelayedTransition(this.parent as ViewGroup, Rotate())
    this.rotation = degrees
}