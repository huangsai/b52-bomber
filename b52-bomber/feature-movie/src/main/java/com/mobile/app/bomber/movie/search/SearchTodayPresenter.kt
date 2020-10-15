package com.mobile.app.bomber.movie.search

import android.view.View
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.databinding.MovieItemSearchTodayBinding
import com.mobile.app.bomber.movie.player.PlayerActivity
import com.mobile.app.bomber.movie.search.items.SearchTodayItem
import com.mobile.guava.android.ui.view.recyclerview.LinearItemDecoration
import com.mobile.guava.jvm.domain.Source
import com.mobile.guava.jvm.extension.exhaustive
import com.pacific.adapter.AdapterUtils
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.RecyclerAdapter
import com.pacific.adapter.SimpleRecyclerItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchTodayPresenter(
        private var activity: SearchActivity,
        private val model: SearchViewModel
) : SimpleRecyclerItem(), View.OnClickListener {

    private var _binding: MovieItemSearchTodayBinding? = null
    private val binding get() = _binding!!

    private var adapter = RecyclerAdapter()

    override fun bind(holder: AdapterViewHolder) {
        _binding = holder.binding(MovieItemSearchTodayBinding::bind)
        binding.recycler.layoutManager = LinearLayoutManager(activity)
        val itemDecoration = LinearItemDecoration.builder(holder.activity())
                .color(R.color.color_text_37374c, R.dimen.size_05dp)
                .build()
        binding.recycler.addItemDecoration(itemDecoration)
        binding.recycler.adapter = adapter
        adapter.onClickListener = this
        requestData()
    }

    private fun requestData() {
        activity.lifecycleScope.launch(Dispatchers.IO) {
            val source = model.getHotKeys()
            withContext(Dispatchers.Main) {
                when (source) {
                    is Source.Success -> {
                        val items = source.requireData().map {
                            SearchTodayItem(it)
                        }
                        adapter.addAll(items)
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
        binding.recycler.layoutManager = null
        binding.recycler.adapter = null
        adapter.clear()
        _binding = null
    }

    override fun getLayout(): Int {
        return R.layout.movie_item_search_today
    }

    @SingleClick
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.item_today_label -> {
                val movie = AdapterUtils.getHolder(v).item<SearchTodayItem>().data.movie
                PlayerActivity.start(activity, movie.movieId.toLong())
            }
        }
    }
}