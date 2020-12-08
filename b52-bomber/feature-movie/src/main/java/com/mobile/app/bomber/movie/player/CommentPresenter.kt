package com.mobile.app.bomber.movie.player

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.os.StrictMode
import android.text.InputType
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import androidx.activity.result.ActivityResultCallback
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.common.base.tool.*
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.base.Phrase3
import com.mobile.app.bomber.movie.base.requireLogin
import com.mobile.app.bomber.movie.databinding.MovieActivityPlayerBinding
import com.mobile.app.bomber.movie.player.items.CommentItem
import com.mobile.ext.glide.GlideApp
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.guava.android.mvvm.showDialogFragment
import com.mobile.guava.jvm.domain.Source
import com.mobile.guava.jvm.extension.exhaustive
import com.pacific.adapter.AdapterUtils
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.RecyclerAdapter
import com.pacific.adapter.SimpleRecyclerItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class CommentPresenter(
        binding: MovieActivityPlayerBinding,
        playerActivity: PlayerActivity,
        model: PlayerViewModel
) : BasePlayerPresenter(binding, playerActivity, model), ShareDialogFragment.CallBack {
    private val adapter = RecyclerAdapter()
    private var commentCount = 0
    private var urlAndBitmap: Bitmap? = null
    private var bgUrl: String = ""

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
        playerActivity.data?.apply {
            binding.txtTitle.text = movie.name
            var nullorEmpty = "0"
            if (movie.playNum.toString().isNullOrEmpty()) {
                nullorEmpty = "0"
            } else {
                val random = (Math.random() * 100).toInt() //生成的随机数
                nullorEmpty = (8000 + movie.playNum * 100 + random).toString()
            }

            var likes = "0"
            if (movie.playNum.toString().isNullOrEmpty()) {
                likes = "0"
            } else {
                likes = movie.likes.toString()
            }
            binding.txtDesc.text = (nullorEmpty + "次播放，影片简介 >")
            binding.txtLike.text = ("点赞  " + likes)
            if (ad == null) {
                binding.layoutGameAd.visibility = View.GONE
            } else {
                if (ad?.title.isNullOrEmpty() && ad?.desc.isNullOrEmpty()) {
                    binding.layoutGameAd.visibility = View.GONE
                } else {
                    binding.layoutGameAd.visibility = View.VISIBLE
                    GlideApp.with(AndroidX.myApp)
                            .load(ad?.img)
                            .placeholder(R.drawable.jq_icon_40)
                            .into(binding.includeGameAd.imgIcon)

                    binding.includeGameAd.txtTitle1.text = ad?.title
                    binding.includeGameAd.txtDesc1.text = ad?.desc
                }
            }
            if (movie.isLike == 0) {//0未点赞 1已点赞
                binding.txtLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.jq_dianzan_weixuanzhong, 0, 0, 0)
            } else if (movie.isLike == 1) {
                binding.txtLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.jq_dianzan, 0, 0, 0)
            }
            if (movie.isCollection == 0) {//0未关注 1已关注
                binding.txtBookmark.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.jq_shoucang, 0, 0)
            } else if (movie.isCollection == 1) {
                binding.txtBookmark.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.jq_shoucang_pre, 0, 0)
            }
        }
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
        playerActivity.data?.apply {
            when (v.id) {
                R.id.edit_comment -> {
                }
                R.id.txt_share -> {
                    shareAppURl()
                }
                R.id.txt_bookmark -> {
                    playerActivity.requireLogin(ActivityResultCallback {
                        if (it.resultCode == Activity.RESULT_OK) {
                            playerActivity.lifecycleScope.launch(Dispatchers.IO) {
                                val isCollection = if (movie.isCollection == 1) {
                                    0
                                } else {
                                    1
                                }
                                val source = model.postMovieCollection(playerActivity.movieId.toInt(), isCollection)
                                withContext(Dispatchers.Main) {
                                    when (source) {
                                        is Source.Success -> {
                                            if (movie.isCollection == 0) {//0未关注 1已关注
                                                movie.isCollection = 1
                                                binding.txtBookmark.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.jq_shoucang_pre, 0, 0)
                                            } else {
                                                movie.isCollection = 0
                                                binding.txtBookmark.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.jq_shoucang, 0, 0)
                                            }
                                        }
                                        else -> Msg.handleSourceException(source.requireError())
                                    }.exhaustive
                                }
                            }
                        }
                    })
                }
                R.id.txt_like -> {
                    playerActivity.requireLogin(ActivityResultCallback {
                        if (it.resultCode == Activity.RESULT_OK) {
                            playerActivity.lifecycleScope.launch(Dispatchers.IO) {
                                val isLike = if (movie.isLike == 1) {
                                    0
                                } else {
                                    1
                                }
                                val source = model.postMovieLike(playerActivity.movieId.toInt(), isLike)
                                withContext(Dispatchers.Main) {
                                    when (source) {
                                        is Source.Success -> {
                                            if (movie.isLike == 0) {//0未点赞 1已点赞
                                                movie.isLike = 1
                                                movie.likes += 1
                                                binding.txtLike.text = ("点赞  " + movie.likes.toString())
                                                binding.txtLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.jq_dianzan, 0, 0, 0)
                                            } else {
                                                movie.isLike = 0
                                                movie.likes -= 1
                                                binding.txtLike.text = ("点赞  " + movie.likes.toString())
                                                binding.txtLike.setCompoundDrawablesWithIntrinsicBounds(R.drawable.jq_dianzan_weixuanzhong, 0, 0, 0)
                                            }
                                        }
                                        else -> Msg.handleSourceException(source.requireError())
                                    }.exhaustive
                                }
                            }
                        }
                    })
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
    }

    override fun load(view: ImageView, holder: AdapterViewHolder) {
        GlideApp.with(AndroidX.myApp)
                .load(holder.item<CommentItem>().data.pic)
                .placeholder(R.drawable.jq_icon_40)
                .into(view)
    }

    private fun shareAppURl() {
        playerActivity.showDialogFragment(ShareDialogFragment.newInstance(this))
    }

    override fun onShareText() {
        playerActivity.lifecycleScope.launch(Dispatchers.IO) {
            val source = model.shareAppUrl()
            withContext(Dispatchers.Main) {
                when (source) {
                    is Source.Success -> {
                        var downurl = source.requireData()
                        val shareURl = downurl.shareUrl
                        var bgUrl = downurl.bgUrl
                        if (TextUtils.isEmpty(shareURl)) {
                            Msg.toast("暂时不能分享")
                        } else {
                            playerActivity.shareToSystem("点击一下 立即拥有", shareURl, null)
                        }
                    }
                    is Source.Error -> {
                        Msg.toast("暂时不能分享")
                        Msg.handleSourceException(source.requireError())
                    }
                }.exhaustive
            }
        }
    }

    override fun onShareImage() {
        playerActivity.lifecycleScope.launch(Dispatchers.IO) {
            val source = model.shareAppUrl()
            withContext(Dispatchers.Main) {
                when (source) {
                    is Source.Success -> {
                        val downurl = source.requireData()
                        val shareURl = downurl.shareUrl
                        val bgUrl = downurl.bgUrl
                        if (TextUtils.isEmpty(shareURl)) {
                            Msg.toast("暂时不能分享")
                        } else {
                            GlideApp.with(AndroidX.myApp).asBitmap().load(bgUrl).into(object : CustomTarget<Bitmap>() {
                                override fun onLoadCleared(placeholder: Drawable?) {

                                }

                                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                                    if (resource == null) {
                                        Msg.toast("地址无效,无法分享")
                                        return
                                    }
                                    val logoQR: Bitmap = QRCodeUtil.createQRCode(shareURl, 560 + 50, 580 + 70)
                                    val bitmap: Bitmap = QRCodeUtil.addTwoLogo(resource, logoQR)
                                    val coverFilePath = FileUtil.saveBitmapToFile(bitmap, "bg_image")
                                    val coverFile = File(coverFilePath)
                                    playerActivity.shareToSystem("点击一下 立即拥有", shareURl, coverFile)
                                }
                            })
                        }
                    }
                    is Source.Error -> {
                        Msg.toast("暂时不能分享")
                        Msg.handleSourceException(source.requireError())
                    }
                }.exhaustive
            }
        }
    }
}