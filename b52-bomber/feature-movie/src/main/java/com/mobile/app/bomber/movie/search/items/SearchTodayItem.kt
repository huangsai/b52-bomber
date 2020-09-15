package com.mobile.app.bomber.movie.search.items

import android.annotation.SuppressLint
import com.mobile.app.bomber.data.http.entities.ApiSearch
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.databinding.MovieItemSearchTodayLabelBinding
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem

class SearchTodayItem(val data: ApiSearch.TodayLabels) : SimpleRecyclerItem() {

    @SuppressLint("SetTextI18n")
    override fun bind(holder: AdapterViewHolder) {
        val binding: MovieItemSearchTodayLabelBinding = holder.binding(MovieItemSearchTodayLabelBinding::bind)
        val pos = holder.bindingAdapterPosition
        binding.title.text = "${pos + 1}、${data.title}"
        binding.rank.text = data.rankDesc
        holder.attachOnClickListener(R.id.item_today_label)
    }

    override fun getLayout(): Int {
        return R.layout.movie_item_search_today_label
    }

}