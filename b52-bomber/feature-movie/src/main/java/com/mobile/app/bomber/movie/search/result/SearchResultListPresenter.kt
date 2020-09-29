package com.mobile.app.bomber.movie.search.result

import android.view.View
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.common.base.RecyclerAdapterEmpty
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.app.bomber.data.http.entities.Pager
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.base.views.MiddleGridItemDecoration
import com.mobile.app.bomber.movie.databinding.MovieItemSearchResultListBinding
import com.mobile.app.bomber.movie.player.PlayerActivity
import com.mobile.app.bomber.movie.search.SearchActivity
import com.mobile.app.bomber.movie.search.SearchViewModel
import com.mobile.app.bomber.movie.top.items.TopMovieVerItem
import com.mobile.ext.glide.GlideApp
import com.mobile.guava.jvm.domain.Source
import com.mobile.guava.jvm.extension.exhaustive
import com.pacific.adapter.AdapterImageLoader
import com.pacific.adapter.AdapterUtils
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchResultListPresenter(
        private var activity: SearchActivity,
        private val model: SearchViewModel,
        private val keyword: String
) : SimpleRecyclerItem(), View.OnClickListener, AdapterImageLoader {

    private val pager = Pager()
    private var _binding: MovieItemSearchResultListBinding? = null
    private val binding get() = _binding!!

    private var adapter = RecyclerAdapterEmpty()

    override fun bind(holder: AdapterViewHolder) {
        _binding = holder.binding(MovieItemSearchResultListBinding::bind)
        val itemDecoration = MiddleGridItemDecoration(holder.activity(), R.dimen.size_6dp)
        val layoutManager = GridLayoutManager(holder.activity(), 2)
        binding.resultRecycler.layoutManager = layoutManager
        binding.resultRecycler.addItemDecoration(itemDecoration)
        adapter.setEmptyView(binding.layoutEmptyView.NoData, binding.resultRecycler)
        binding.resultRecycler.adapter = adapter
        adapter.onClickListener = this
        adapter.imageLoader = this
        requestData()
    }

    private fun requestData() {
        activity.lifecycleScope.launch(Dispatchers.IO) {
            val source = model.searchMovie(keyword, pager)
            withContext(Dispatchers.Main) {
                when (source) {
                    is Source.Success -> {
                        val items = source.data.map {
                            TopMovieVerItem(it)
                        }
                        adapter.replaceAll(items)
                    }
                    is Source.Error -> {
                        Msg.handleSourceException(source.requireError())
                    }
                }.exhaustive
            }
        }
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
    override fun onClick(v: View) {
        when (v.id) {
            R.id.item_top_movie_ver -> {
                val data = AdapterUtils.getHolder(v).item<TopMovieVerItem>().data
                activity.lifecycleScope.launch(Dispatchers.IO) {
                    model.playSearchMovie(data.movieId)
                }
                PlayerActivity.start(activity, data)
            }
        }
    }

    override fun load(view: ImageView, holder: AdapterViewHolder) {
        val data = AdapterUtils.getHolder(view).item<TopMovieVerItem>().data
        GlideApp.with(activity)
                .load(data.cover)
                .placeholder(R.drawable.movie_default_cover)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view)
    }
}