package com.mobile.app.bomber.movie.search.items

import android.annotation.SuppressLint
import com.mobile.app.bomber.data.http.entities.ApiMovieHotKey
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.databinding.MovieItemSearchTodayLabelBinding
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem

class SearchTodayItem(val data: ApiMovieHotKey.MovieHotKey) : SimpleRecyclerItem() {

    override fun bind(holder: AdapterViewHolder) {
        val binding: MovieItemSearchTodayLabelBinding = holder.binding(MovieItemSearchTodayLabelBinding::bind)
        val pos = holder.bindingAdapterPosition
        binding.title.text = "${pos + 1}、${data.name}"
        when (data.hotType) { // 0 下降 1 持平 2 上升
            0 -> binding.rank.text = "下降"
            1 -> binding.rank.text = "持平"
            2 -> binding.rank.text = "上升"
        }
        holder.attachOnClickListener(R.id.item_today_label)
    }

    override fun getLayout(): Int {
        return R.layout.movie_item_search_today_label
    }
}