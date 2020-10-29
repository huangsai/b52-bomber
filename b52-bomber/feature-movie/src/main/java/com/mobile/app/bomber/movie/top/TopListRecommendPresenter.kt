package com.mobile.app.bomber.movie.top

import android.view.View
import android.widget.ImageView
import androidx.compose.runtime.invalidate
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.movie.MovieViewModel
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.databinding.MovieItemTopVerBinding
import com.mobile.app.bomber.movie.player.PlayerActivity
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
 * 精彩一级页面-为你推荐列表
 */
class TopListRecommendPresenter(
        private val fragment: TopListFragment,
        private val model: MovieViewModel
) : BaseTopMoviePresenter(fragment.requireContext(), true) {
    override fun load() {
        fragment.lifecycleScope.launch(Dispatchers.IO) {
            val source = model.getMovieListRecommend()
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
        PlayerActivity.start(fragment.requireActivity(), data.movieId.toLong())
    }

}