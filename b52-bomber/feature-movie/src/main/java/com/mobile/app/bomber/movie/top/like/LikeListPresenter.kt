package com.mobile.app.bomber.movie.top.like

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mobile.ext.glide.GlideApp
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.data.http.entities.ApiMovie
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.top.BaseTopMoviePresenter
import com.mobile.app.bomber.movie.top.items.TopMovieVerItem
import com.pacific.adapter.AdapterViewHolder

/**
 * 猜你喜欢-猜你喜欢列表
 */
class LikeListPresenter(context: Context) : BaseTopMoviePresenter(context, true) {

    override fun load() {
        val items = ArrayList<TopMovieVerItem>()
        items.add(TopMovieVerItem(ApiMovie.Movie(1)))
        items.add(TopMovieVerItem(ApiMovie.Movie(1)))
        items.add(TopMovieVerItem(ApiMovie.Movie(1)))
        items.add(TopMovieVerItem(ApiMovie.Movie(1)))
        items.add(TopMovieVerItem(ApiMovie.Movie(1)))
        items.add(TopMovieVerItem(ApiMovie.Movie(1)))
        items.add(TopMovieVerItem(ApiMovie.Movie(1)))
        items.add(TopMovieVerItem(ApiMovie.Movie(1)))
        items.add(TopMovieVerItem(ApiMovie.Movie(1)))
        items.add(TopMovieVerItem(ApiMovie.Movie(1)))
        adapter.addAll(items)
    }

    override fun load(imageView: ImageView, holder: AdapterViewHolder) {
        GlideApp.with(context)
                .load("")
                .placeholder(R.drawable.movie_default_cover)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView)
    }

    override fun onClick(v: View) {
        Msg.toast("点击了视频")
    }
}