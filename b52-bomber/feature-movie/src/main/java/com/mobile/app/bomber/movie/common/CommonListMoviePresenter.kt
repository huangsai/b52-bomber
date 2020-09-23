package com.mobile.app.bomber.movie.common

import android.content.Context
import android.view.View
import android.widget.ImageView
import androidx.annotation.CallSuper
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mobile.ext.glide.GlideApp
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.common.base.RecyclerAdapterEmpty
import com.mobile.app.bomber.data.http.entities.ApiMovie
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.common.items.CommonMovieItem
import com.mobile.app.bomber.movie.databinding.MovieItemCommonListBinding
import com.mobile.guava.android.ui.view.recyclerview.LinearItemDecoration
import com.pacific.adapter.AdapterImageLoader
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem

class CommonListMoviePresenter(private var context: Context) :
        SimpleRecyclerItem(), AdapterImageLoader, View.OnClickListener {

    private lateinit var binding: MovieItemCommonListBinding
    private val adapter = RecyclerAdapterEmpty()

    override fun bind(holder: AdapterViewHolder) {
        binding = holder.binding(MovieItemCommonListBinding::bind)
        val itemDecoration = LinearItemDecoration.builder(holder.activity())
                .color(android.R.color.transparent, R.dimen.size_28dp)
                .build()
        val layoutManager = LinearLayoutManager(
                holder.activity(),
                LinearLayoutManager.VERTICAL,
                false)
        binding.recycler.layoutManager = layoutManager
        binding.recycler.addItemDecoration(itemDecoration)
        adapter.setEmptyView(binding.txtEmpty, binding.recycler)
        adapter.imageLoader = this
        adapter.onClickListener = this
        binding.recycler.adapter = adapter
        load()
    }

    private fun load() {
        val items = ArrayList<CommonMovieItem>()
        items.add(CommonMovieItem(ApiMovie.Movie(1)))
        items.add(CommonMovieItem(ApiMovie.Movie(1)))
        items.add(CommonMovieItem(ApiMovie.Movie(1)))
        items.add(CommonMovieItem(ApiMovie.Movie(1)))
        items.add(CommonMovieItem(ApiMovie.Movie(1)))
        items.add(CommonMovieItem(ApiMovie.Movie(1)))
        items.add(CommonMovieItem(ApiMovie.Movie(1)))
        adapter.addAll(items)
    }

    override fun getLayout(): Int {
        return R.layout.movie_item_common_list
    }

    @CallSuper
    override fun unbind(holder: AdapterViewHolder) {
        super.unbind(holder)
        binding.recycler.layoutManager = null
        binding.recycler.adapter = null
    }

    override fun load(imageView: ImageView, holder: AdapterViewHolder) {
        GlideApp.with(context)
                .load("")
                .placeholder(R.drawable.movie_default_cover)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView)
    }

    override fun onClick(v: View) {
        Msg.toast("点击了视频")
    }

}