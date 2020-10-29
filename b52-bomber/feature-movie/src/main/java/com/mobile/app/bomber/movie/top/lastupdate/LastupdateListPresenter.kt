package com.mobile.app.bomber.movie.top.lastupdate

import android.view.View
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.data.http.entities.Pager
import com.mobile.app.bomber.movie.MovieViewModel
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.player.PlayerActivity
import com.mobile.app.bomber.movie.top.BaseTopMoviePresenter
import com.mobile.app.bomber.movie.top.items.TopMovieVerItem
import com.mobile.ext.glide.GlideApp
import com.mobile.guava.jvm.domain.Source
import com.mobile.guava.jvm.extension.exhaustive
import com.pacific.adapter.AdapterUtils
import com.pacific.adapter.AdapterViewHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 为你推荐-小编力推列表
 */
class LastupdateListPresenter(
        private val activity: TopLastupdateActivity,
        private val model: MovieViewModel
) : BaseTopMoviePresenter(activity, true) {
    protected val pager = Pager()
    override fun load() {
        activity.lifecycleScope.launch(Dispatchers.IO) {
            val source = model.getMovieListLastUpdate(pager)
            val items = source.dataOrNull().orEmpty().map { TopMovieVerItem(it) }
            withContext(Dispatchers.Main) {
                when (source) {
                    is Source.Success -> {
                        adapter.replaceAll(items)
                    }
                    is Source.Error -> {
                        Msg.handleSourceException(source.requireError())
                    }
                }.exhaustive
            }
        }
    }

    override fun load(imageView: ImageView, holder: AdapterViewHolder) {
        val data = AdapterUtils.getHolder(imageView).item<TopMovieVerItem>().data
        GlideApp.with(context)
                .load(data.cover)
                .placeholder(R.drawable.movie_default_cover)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView)
    }

    override fun onRefresh() {
        load()
    }

    override fun onClick(v: View) {
        val data = AdapterUtils.getHolder(v).item<TopMovieVerItem>().data
        PlayerActivity.start(activity, data.movieId.toLong())
    }
}