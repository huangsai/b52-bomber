package com.mobile.app.bomber.movie.player.items

import android.text.style.ForegroundColorSpan
import android.view.View
import com.mobile.app.bomber.data.http.entities.ApiComment
import com.mobile.app.bomber.data.repository.forPage
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.databinding.MovieItemCommentABinding
import com.mobile.app.bomber.movie.databinding.MovieItemCommentBBinding
import com.mobile.app.bomber.movie.databinding.MovieItemCommentCBinding
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.app.bomber.runner.base.PrefsManager
import com.mobile.guava.android.context.toColor
import com.mobile.guava.android.ui.view.text.MySpannable
import com.mobile.guava.jvm.date.ago
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem

abstract class CommentItem(val data: ApiComment.Comment, val type: Int) : SimpleRecyclerItem() {

    protected val isMe: Boolean = if (type == 3) false else PrefsManager.getUserId() == data.uid
    protected val spannableComment: MySpannable by lazy {
        val userColor = AndroidX.myApp.toColor(R.color.movie_player_comment_user)
        val commentColor = AndroidX.myApp.toColor(R.color.movie_player_game_ad_desc)
        val ago = ago(data.time, System.currentTimeMillis())
        val replay = data.replayText

        return@lazy MySpannable(replay + data.content + "\u3000" + ago)
                .findAndSpan(replay) { ForegroundColorSpan(userColor) }
                .findAndSpan(ago) { ForegroundColorSpan(commentColor) }
                .apply {
                    data.atTexts.forEach {
                        findAndSpan(it) { ForegroundColorSpan(userColor) }
                    }
                }
    }

    class TypeA(data: ApiComment.Comment) : CommentItem(data, 1) {

        override fun bind(holder: AdapterViewHolder) {
            val binding: MovieItemCommentABinding = holder.binding(MovieItemCommentABinding::bind)
            binding.txtName.text = data.username
            if (isMe) {
                binding.txtLabel.visibility = View.VISIBLE
            } else {
                binding.txtLabel.visibility = View.INVISIBLE
            }
            binding.txtComment.text = spannableComment
            binding.txtLike.isSelected = data.isLiking
            binding.txtLike.text = data.likeCount.toString()

            holder.attachImageLoader(R.id.img_profile)
            holder.attachOnClickListener(R.id.txt_like)
            holder.attachOnClickListener(R.id.item_comment_a)
            holder.attachOnLongClickListener(R.id.item_comment_a)
        }

        override fun bindPayloads(holder: AdapterViewHolder, payloads: List<Any>?) {
            val binding: MovieItemCommentABinding = holder.binding()
            binding.txtLike.isSelected = data.isLiking
            binding.txtLike.text = data.likeCount.toString()
        }

        override fun getLayout(): Int = R.layout.movie_item_comment_a
    }

    class TypeB(data: ApiComment.Comment) : CommentItem(data, 2) {
        override fun bind(holder: AdapterViewHolder) {
            val binding: MovieItemCommentBBinding = holder.binding(MovieItemCommentBBinding::bind)
            binding.txtName.text = data.username
            if (isMe) {
                binding.txtLabel.visibility = View.VISIBLE
            } else {
                binding.txtLabel.visibility = View.INVISIBLE
            }
            binding.txtComment.text = spannableComment
            binding.txtLike.isSelected = data.isLiking
            binding.txtLike.text = data.likeCount.toString()
            holder.attachImageLoader(R.id.img_profile)
            holder.attachOnClickListener(R.id.txt_like)
            holder.attachOnClickListener(R.id.item_comment_b)
            holder.attachOnLongClickListener(R.id.item_comment_b)
        }

        override fun bindPayloads(holder: AdapterViewHolder, payloads: List<Any>?) {
            val binding: MovieItemCommentBBinding = holder.binding()
            binding.txtLike.isSelected = data.isLiking
            binding.txtLike.text = data.likeCount.toString()
        }

        override fun getLayout(): Int = R.layout.movie_item_comment_b
    }

    class TypeC(data: ApiComment.Comment) : CommentItem(data, 3) {

        private val list: List<ApiComment.Comment>
        private var hidingCount = 0
        private var showingCount = 0
        private var lastShowingPage = 0

        init {
            require(!data.children.isNullOrEmpty())
            list = data.children!!.subList(SLOT_COUNT, data.children!!.size)
            hidingCount = list.size
            showingCount = 0
            lastShowingPage = 0
        }

        override fun bind(holder: AdapterViewHolder) {
            val binding: MovieItemCommentCBinding = holder.binding(MovieItemCommentCBinding::bind)
            setComment(binding)
            holder.attachOnClickListener(R.id.item_comment_c)
        }

        override fun bindPayloads(holder: AdapterViewHolder, payloads: List<Any>?) {
            val binding: MovieItemCommentCBinding = holder.binding()
            setComment(binding)
        }

        override fun getLayout(): Int = R.layout.movie_item_comment_c

        private fun setComment(binding: MovieItemCommentCBinding) {
            val comment: String = when {
                list.size === hidingCount -> "展开" + list.size.toString() + "条回复"
                hidingCount === 0 -> "收起"
                else -> "展开更多回复"
            }
            binding.txtComment.text = comment
        }

        fun next(): List<ApiComment.Comment> {
            val next = list.forPage(lastShowingPage + 1, SLOT_COUNT)
            if (next.isNotEmpty()) {
                lastShowingPage++
                showingCount += next.size
                hidingCount -= next.size
            }
            return next
        }

        fun size(): Int = list.size

        fun reset() {
            hidingCount = list.size
            showingCount = 0
            lastShowingPage = 0
        }
    }

    class TypeD : SimpleRecyclerItem() {

        override fun bind(adapterViewHolder: AdapterViewHolder) {}

        override fun getLayout(): Int = R.layout.movie_item_comment_d
    }

    companion object {

        @get:JvmName("SLOT_COUNT")
        const val SLOT_COUNT = 3
    }
}