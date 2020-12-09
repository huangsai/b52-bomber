package com.mobile.app.bomber.movie.top

import android.view.View
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.movie.MovieViewModel
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.player.PlayerActivity
import com.mobile.app.bomber.movie.top.items.TopMovieLikeItem
import com.mobile.ext.glide.GlideApp
import com.mobile.guava.jvm.domain.Source
import com.mobile.guava.jvm.extension.exhaustive
import com.pacific.adapter.AdapterUtils
import com.pacific.adapter.AdapterViewHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 精彩一级页面-猜你喜欢列表
 */

class TopListLikePresenter(
        private val fragment: TopListFragment,
        private val model: MovieViewModel
) : BaseMoviePresenter(fragment.requireContext(), false) {

    override fun load() {
        fragment.lifecycleScope.launch(Dispatchers.IO) {
            val source = model.getMovieGuessLike()
            val items = source.dataOrNull().orEmpty().map { TopMovieLikeItem(it) }
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

    override fun load(view: ImageView, holder: AdapterViewHolder) {
        val data = AdapterUtils.getHolder(view).item<TopMovieLikeItem>().data
        GlideApp.with(context)
                .load(data.cover)
                .placeholder(R.drawable.movie_default_cover)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view)
    }

    override fun onRefresh() {
        load()
    }

    override fun onClick(v: View) {
        val data = AdapterUtils.getHolder(v).item<TopMovieLikeItem>().data
        PlayerActivity.start(fragment.requireActivity(), data.movieId.toLong())
    }

}