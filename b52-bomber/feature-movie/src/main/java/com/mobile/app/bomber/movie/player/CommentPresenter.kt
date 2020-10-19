package com.mobile.app.bomber.movie.player

import android.text.InputType
import android.view.View
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.common.base.tool.shareToSystem
import com.mobile.app.bomber.data.http.entities.Pager
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.base.Phrase3
import com.mobile.app.bomber.movie.databinding.MovieActivityPlayerBinding
import com.mobile.app.bomber.movie.player.items.CommentItem
import com.mobile.ext.glide.GlideApp
import com.mobile.guava.jvm.domain.Source
import com.mobile.guava.jvm.extension.exhaustive
import com.pacific.adapter.AdapterUtils
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.RecyclerAdapter
import com.pacific.adapter.SimpleRecyclerItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommentPresenter(
        binding: MovieActivityPlayerBinding,
        playerActivity: PlayerActivity,
        model: PlayerViewModel
) : BasePlayerPresenter(binding, playerActivity, model) {

    private val pager = Pager()
    private val adapter = RecyclerAdapter()
    private var commentCount = 0

    init {
        binding.editComment.inputType = InputType.TYPE_NULL
        binding.editComment.setOnClickListener(this)
        binding.txtShare.setOnClickListener(this)
        binding.txtBookmark.setOnClickListener(this)
        binding.txtDownload.setOnClickListener(this)
        binding.txtLike.setOnClickListener(this)

        binding.recycler.layoutManager = LinearLayoutManager(playerActivity)
        binding.recycler.adapter = adapter
        adapter.onClickListener = this
        adapter.imageLoader = this
    }

    override fun onCreate() {
        requestComment()
    }

    private fun requestComment() {
        playerActivity.lifecycleScope.launch(Dispatchers.IO) {
            val source = model.requestComment()
            val list = ArrayList<SimpleRecyclerItem>()
            var childIndex: Int
            commentCount = 0
            source.forEach { o ->
                list.add(CommentItem.TypeA(o))
                commentCount++
                childIndex = 0
                o.recursiveChildren(o).forEach { oo ->
                    when {
                        childIndex < CommentItem.SLOT_COUNT -> {
                            list.add(CommentItem.TypeB(oo))
                            commentCount++
                            childIndex++
                        }
                        childIndex == CommentItem.SLOT_COUNT -> {
                            list.add(CommentItem.TypeC(o))
                            childIndex++
                        }
                    }
                }
            }
            list.add(CommentItem.TypeD())
            withContext(Dispatchers.Main) {
                adapter.replaceAll(list)
                binding.txtCommentDesc.text = Phrase3.format(R.string.movie_player_comment_count)
                        .with("count", commentCount)
                        .build()
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.edit_comment -> {
            }
            R.id.txt_share -> {
                playerActivity.shareToSystem("http://www.google.com")
            }
            R.id.txt_bookmark -> {
                playerActivity.lifecycleScope.launch(Dispatchers.IO) {
                    val source = model.postMovieCollection(playerActivity.movieId.toInt(), 0)
                    withContext(Dispatchers.Main) {
                        when (source) {
                            is Source.Success -> {

                            }
                            else -> Msg.handleSourceException(source.requireError())
                        }.exhaustive
                    }
                }
            }
            R.id.txt_like -> {
                playerActivity.lifecycleScope.launch(Dispatchers.IO) {
                    val source = model.postMovieLike(playerActivity.movieId.toInt(), 1)
                    withContext(Dispatchers.Main) {
                        when (source) {
                            is Source.Success -> {

                            }
                            else -> Msg.handleSourceException(source.requireError())
                        }.exhaustive
                    }
                }
            }
            R.id.item_comment_c -> {
                val holder = AdapterUtils.getHolder(v)
                val typeC = holder.item<CommentItem.TypeC>()
                val next = typeC.next().map {
                    CommentItem.TypeB(it)
                }
                val position = holder.bindingAdapterPosition
                if (next.isEmpty()) {
                    adapter.removeAll(adapter.getAll().subList(position - typeC.size(), position))
                    typeC.reset()
                } else {
                    adapter.addAll(position, next)
                }
                adapter.notifyItemChanged(adapter.indexOf(typeC))
            }
        }
    }

    override fun load(view: ImageView, holder: AdapterViewHolder) {
        GlideApp.with(playerActivity)
                .load(holder.item<CommentItem>().data.pic)
                .placeholder(R.drawable.jq_icon_40)
                .into(view)
    }
}