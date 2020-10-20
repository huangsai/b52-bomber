package com.mobile.app.bomber.movie.top

import androidx.fragment.app.Fragment
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.databinding.MovieItemSearchBinding
import com.mobile.app.bomber.movie.search.SearchActivity
import com.mobile.guava.android.mvvm.newStartActivity
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem

class MovieSearchPresenter(private val fragment: Fragment) : SimpleRecyclerItem() {

    override fun bind(holder: AdapterViewHolder) {
        val binding: MovieItemSearchBinding = holder.binding(MovieItemSearchBinding::bind)
        binding.searchToolbar.setOnClickListener {
            fragment.newStartActivity(SearchActivity::class.java)
        }
    }

    override fun getLayout(): Int {
        return R.layout.movie_item_search
    }
}