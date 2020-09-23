package com.mobile.app.bomber.movie.search.result

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mobile.ext.glide.GlideApp
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.app.bomber.data.http.entities.ApiMovie
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.base.views.MiddleGridItemDecoration
import com.mobile.app.bomber.movie.databinding.MovieItemSearchResultListBinding
import com.mobile.app.bomber.movie.search.SearchActivity
import com.mobile.app.bomber.movie.top.items.TopMovieVerItem
import com.pacific.adapter.AdapterImageLoader
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.RecyclerAdapter
import com.pacific.adapter.SimpleRecyclerItem

class SearchResultListPresenter(private var activity: SearchActivity) : SimpleRecyclerItem(), View.OnClickListener, AdapterImageLoader {
    private var _binding: MovieItemSearchResultListBinding? = null
    private val binding get() = _binding!!

    private var adapter = RecyclerAdapter()

    override fun bind(holder: AdapterViewHolder) {
        _binding = holder.binding(MovieItemSearchResultListBinding::bind)
        val itemDecoration = MiddleGridItemDecoration(holder.activity(), R.dimen.size_6dp)
        val layoutManager = GridLayoutManager(holder.activity(), 2)
        binding.resultRecycler.layoutManager = layoutManager
        binding.resultRecycler.addItemDecoration(itemDecoration)
        binding.resultRecycler.adapter = adapter
        adapter.onClickListener = this
        adapter.imageLoader = this
        requestData()
    }

    private fun requestData() {
        val items = listOf(
                ApiMovie.Movie(1),
                ApiMovie.Movie(1),
                ApiMovie.Movie(1),
                ApiMovie.Movie(1),
                ApiMovie.Movie(1),
                ApiMovie.Movie(1)
        ).map { TopMovieVerItem(it) }
        adapter.addAll(items)
    }

    override fun unbind(holder: AdapterViewHolder) {
        super.unbind(holder)
        binding.resultRecycler.layoutManager = null
        binding.resultRecycler.adapter = null
        adapter.clear()
        _binding = null
    }

    override fun getLayout(): Int {
        return R.layout.movie_item_search_result_list
    }

    @SingleClick
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.item_top_movie_ver -> {
                Msg.toast("播放视频")
            }
        }
    }

    override fun load(view: ImageView, holder: AdapterViewHolder) {
        GlideApp.with(activity)
                .load("")
                .placeholder(R.drawable.movie_default_cover)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view)
    }
}