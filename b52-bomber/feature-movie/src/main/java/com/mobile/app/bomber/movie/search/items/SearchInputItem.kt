package com.mobile.app.bomber.movie.search.items

import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.databinding.MovieItemSearchInputBinding
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem

class SearchInputItem(val data: String) : SimpleRecyclerItem() {
    private var _binding: MovieItemSearchInputBinding? = null
    private val binding get() = _binding!!

    override fun bind(holder: AdapterViewHolder) {
        _binding = holder.binding(MovieItemSearchInputBinding::bind)
        binding.content.text = data
        holder.attachOnClickListener(R.id.item_search_input)
    }

    override fun unbind(holder: AdapterViewHolder) {
        super.unbind(holder)
        _binding = null
    }

    override fun getLayout(): Int {
        return R.layout.movie_item_search_input
    }
}