package com.mobile.app.bomber.movie.search

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.app.bomber.data.http.entities.ApiSearch
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.databinding.MovieItemSearchTodayBinding
import com.mobile.app.bomber.movie.search.items.SearchTodayItem
import com.mobile.guava.android.ui.view.recyclerview.LinearItemDecoration
import com.pacific.adapter.AdapterUtils
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.RecyclerAdapter
import com.pacific.adapter.SimpleRecyclerItem

class SearchTodayPresenter(private var activity: SearchActivity) : SimpleRecyclerItem(), View.OnClickListener {
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
        val labels = listOf(
                ApiSearch.TodayLabels("奔跑吧，少年", "上升"),
                ApiSearch.TodayLabels("西红柿首富", "上升"),
                ApiSearch.TodayLabels("八百", "持平"),
                ApiSearch.TodayLabels("肖申克救赎", "上升"),
                ApiSearch.TodayLabels("做一个好人", "持平"),
                ApiSearch.TodayLabels("狼狈为奸", "上升"),
                ApiSearch.TodayLabels("狼狈为奸", "上升"),
                ApiSearch.TodayLabels("狼狈为奸", "上升"),
                ApiSearch.TodayLabels("狼狈为奸", "上升"),
                ApiSearch.TodayLabels("狼狈为奸", "上升")
        )
        val items = labels.map { SearchTodayItem(it) }
        adapter.addAll(items)
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
                val pos = AdapterUtils.getHolder(v).bindingAdapterPosition
                val title = adapter.get<SearchTodayItem>(pos).data.title
                activity.setInputContent(title)
            }
        }
    }
}