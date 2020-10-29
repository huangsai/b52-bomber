package com.mobile.app.bomber.movie.top

import android.view.View
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.data.http.entities.ApiMovie
import com.mobile.app.bomber.movie.MovieViewModel
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.player.PlayerActivity
import com.mobile.app.bomber.movie.top.items.TopMovieNearItem
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
class TopListNearPresenter(
        private val fragment: TopListFragment,
        private val model: MovieViewModel
) : BaseTopMoviePresenter(fragment.requireContext(), true) {

    override fun load() {
          adapter.clear()
//        fragment.lifecycleScope.launch(Dispatchers.IO) {
//            val source = model.getMovieListRecommend()
//            val items = source.dataOrNull().orEmpty().map { TopMovieNearItem(it) }
//            withContext(Dispatchers.Main) {
//                when (source) {
//                    is Source.Success -> {
////                        adapter.replaceAll(items)
//                    }
//                    is Source.Error -> {
//                        Msg.handleSourceException(source.requireError())
//                    }
//                }.exhaustive
//            }
//        }
       var m =  ApiMovie.Movie(1,1,"1","1",1
               ,1,1,1,1,
               1,1,1,"1",1,1,
               1,1,1,"1","1",
               "1","1","1",1,1)
        var item = TopMovieNearItem(m)
        var lsit = listOf(item,item,item,item,item)
        adapter.replaceAll(lsit)

    }

    override fun load(imageView: ImageView, holder: AdapterViewHolder) {
        val data = AdapterUtils.getHolder(imageView).item<TopMovieNearItem>().data
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
        val data = AdapterUtils.getHolder(v).item<TopMovieNearItem>().data
        PlayerActivity.start(fragment.requireActivity(), data.movieId.toLong())
    }

}