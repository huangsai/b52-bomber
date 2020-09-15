package com.mobile.app.bomber.movie.player

import android.view.View
import com.mobile.app.bomber.movie.databinding.MovieActivityPlayerBinding
import com.mobile.guava.android.mvvm.lifecycle.SimplePresenter
import com.pacific.adapter.AdapterImageLoader

abstract class BasePlayerPresenter(
        protected val binding: MovieActivityPlayerBinding,
        protected val playerActivity: PlayerActivity,
        protected val model: PlayerViewModel
) : SimplePresenter(), View.OnClickListener, AdapterImageLoader {
}