package com.mobile.app.bomber.movie.top.recommend

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.top.BaseMoviePresenter
import com.mobile.ext.glide.GlideApp
import com.pacific.adapter.AdapterViewHolder

/**
 * 为你推荐-好友观看列表
 */
class HistoryFriendPresenter(context: Context) : BaseMoviePresenter(context, true) {

    override fun load() {
    }

    override fun load(view: ImageView, holder: AdapterViewHolder) {
        GlideApp.with(context)
                .load("")
                .placeholder(R.drawable.movie_default_cover)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view)
    }

    override fun onRefresh() {
        TODO("Not yet implemented")
    }

    override fun onClick(v: View) {
        Msg.toast("点击了视频")
    }
}