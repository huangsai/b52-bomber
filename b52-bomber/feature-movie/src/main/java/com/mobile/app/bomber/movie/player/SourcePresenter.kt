package com.mobile.app.bomber.movie.player

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.GridLayoutManager
import com.mobile.app.bomber.common.base.GlideApp
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.base.animRotate
import com.mobile.app.bomber.movie.databinding.MovieActivityPlayerBinding
import com.mobile.app.bomber.movie.player.items.ActorItem
import com.mobile.guava.android.ui.view.expandable.ExpandableLayout2
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.RecyclerAdapter

class SourcePresenter(
        binding: MovieActivityPlayerBinding,
        playerActivity: PlayerActivity,
        model: PlayerViewModel
) : BasePlayerPresenter(binding, playerActivity, model), ExpandableLayout2.OnExpansionUpdateListener {

    private val adapter = RecyclerAdapter()
    private var isAnimating = false

    init {
        adapter.onClickListener = this
        adapter.imageLoader = this

        binding.includeMovieInfo.recycler.layoutManager = GridLayoutManager(playerActivity, 5)
        binding.includeMovieInfo.recycler.adapter = adapter

        binding.includeMovieInfo.layoutMovieInfo.setOnExpansionUpdateListener(this)
        binding.imgDetailArrow.setOnClickListener(this)
        binding.imgDetailArrow.animRotate(0f)
    }

    override fun onCreate() {
        requestMovieInfo()
    }

    private fun requestMovieInfo() {
        binding.includeMovieInfo.txtLabel.text = listOf("国产电影", "国产电影", "国产电影", "国产电影")
                .joinToString("\u3000")
        binding.includeMovieInfo.txtDetail.text = "国产电影,国产电影,国产电影\n国产电影,国产电影,国产电影"

        adapter.addAll(listOf(
                ActorItem(), ActorItem(), ActorItem(), ActorItem(),
                ActorItem(), ActorItem(), ActorItem(), ActorItem()
        ))
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.img_detail_arrow -> {
                if (isAnimating) return
                isAnimating = true
                if (binding.includeMovieInfo.layoutMovieInfo.isExpanded) {
                    binding.imgDetailArrow.animRotate(0f)
                } else {
                    binding.imgDetailArrow.animRotate(180f)
                }
                binding.includeMovieInfo.layoutMovieInfo.toggle()
            }
        }
    }

    override fun load(view: ImageView, holder: AdapterViewHolder) {
        GlideApp.with(playerActivity)
                .load("")
                .placeholder(R.drawable.jq_icon_40)
                .into(view)
    }

    override fun onExpansionUpdate(expansionFraction: Float, state: Int) {
        when (state) {
            ExpandableLayout2.State.COLLAPSED -> {
                isAnimating = false
            }
            ExpandableLayout2.State.EXPANDED -> {
                isAnimating = false
            }
        }
    }
}