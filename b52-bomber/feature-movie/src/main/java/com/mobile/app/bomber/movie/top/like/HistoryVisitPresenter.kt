package com.mobile.app.bomber.movie.top.like

import android.content.Context
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.top.BaseTopMoviePresenter
import com.mobile.ext.glide.GlideApp
import com.pacific.adapter.AdapterViewHolder

/**
 * 猜你喜欢-历史观看列表
 */
class HistoryVisitPresenter(context: Context) : BaseTopMoviePresenter(context, true) {

    override fun load() {
    }

    override fun load(imageView: ImageView, holder: AdapterViewHolder) {
        GlideApp.with(context)
                .load("")
                .placeholder(R.drawable.movie_default_cover)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView)
    }

    override fun onRefresh() {
        TODO("Not yet implemented")
    }

    override fun onClick(v: View) {
        Msg.toast("点击了视频")
    }
}