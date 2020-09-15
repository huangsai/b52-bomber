package com.mobile.app.bomber.movie

import com.mobile.app.bomber.movie.databinding.MovieItemLabelBinding
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem

class MovieLabelItem(val data: String) : SimpleRecyclerItem() {
    override fun bind(holder: AdapterViewHolder) {
        val binding: MovieItemLabelBinding = holder.binding(MovieItemLabelBinding::bind)
        binding.itemLabelText.text = data
        holder.attachOnClickListener(R.id.item_label_text)
    }

    override fun getLayout(): Int {
        return R.layout.movie_item_label
    }

}