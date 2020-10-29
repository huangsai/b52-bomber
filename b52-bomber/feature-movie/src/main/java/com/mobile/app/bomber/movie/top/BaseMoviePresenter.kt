package com.mobile.app.bomber.movie.top

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.mobile.app.bomber.common.base.RecyclerAdapterEmpty
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.base.views.MiddleGridItemDecoration
import com.mobile.app.bomber.movie.databinding.MovieItemTopListBinding
import com.mobile.guava.android.ui.view.recyclerview.LinearItemDecoration
import com.pacific.adapter.AdapterImageLoader
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem

abstract class BaseMoviePresenter(protected val context: Context, private val isVertical: Boolean) :
        SimpleRecyclerItem(), AdapterImageLoader, View.OnClickListener {

    private lateinit var itemDecoration: RecyclerView.ItemDecoration
    private lateinit var layoutManager: RecyclerView.LayoutManager
    protected val adapter = RecyclerAdapterEmpty()

    override fun bind(holder: AdapterViewHolder) {
        val binding = holder.binding(MovieItemTopListBinding::bind)
        if (binding.recycler.layoutManager == null) {
            if (isVertical) {
                itemDecoration = MiddleGridItemDecoration(holder.activity(), R.dimen.size_6dp)
                layoutManager = GridLayoutManager(holder.activity(), 2)
            } else {
                itemDecoration = LinearItemDecoration.builder(holder.activity())
                        .color(android.R.color.transparent, R.dimen.size_6dp)
                        .horizontal()
                        .build()
                layoutManager = LinearLayoutManager(
                        holder.activity(),
                        LinearLayoutManager.HORIZONTAL,
                        false)
            }
            binding.recycler.layoutManager = layoutManager
            binding.recycler.addItemDecoration(itemDecoration)
            adapter.setEmptyView(binding.txtEmpty, binding.recycler)
            adapter.imageLoader = this
            adapter.onClickListener = this
            binding.recycler.adapter = adapter
            load()
        }
    }

    override fun unbind(holder: AdapterViewHolder) {
        super.unbind(holder)
        val binding: MovieItemTopListBinding = holder.binding()
        binding.recycler.layoutManager = null
        binding.recycler.adapter = null
    }

    override fun getLayout(): Int {
        return R.layout.movie_item_top_list
    }

    protected abstract fun load()

    abstract fun onRefresh()
}