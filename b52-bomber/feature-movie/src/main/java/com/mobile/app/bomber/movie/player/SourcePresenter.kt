package com.mobile.app.bomber.movie.player

import android.view.View
import android.widget.ImageView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.app.bomber.data.http.entities.ApiMovieDetailById
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.base.animRotate
import com.mobile.app.bomber.movie.databinding.MovieActivityPlayerBinding
import com.mobile.app.bomber.movie.player.items.ActorItem
import com.mobile.ext.glide.GlideApp
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.guava.android.ui.view.expandable.ExpandableLayout2
import com.mobile.guava.jvm.domain.Source
import com.mobile.guava.jvm.extension.exhaustive
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.RecyclerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SourcePresenter(
        binding: MovieActivityPlayerBinding,
        playerActivity: PlayerActivity,
        model: PlayerViewModel
) : BasePlayerPresenter(binding, playerActivity, model), ExpandableLayout2.OnExpansionUpdateListener {

    private val adapter = RecyclerAdapter()
    private var isAnimating = false
    var dataDetail: ApiMovieDetailById? = null
        private set
    var detail: ApiMovieDetailById.Detail? = null
        private set
    var movieId: Long = 0L

    init {
        adapter.onClickListener = this
        adapter.imageLoader = this

        binding.includeMovieInfo.recycler.layoutManager = GridLayoutManager(playerActivity, 5)
        binding.includeMovieInfo.recycler.adapter = adapter

        binding.includeMovieInfo.layoutMovieInfo.setOnExpansionUpdateListener(this)
        binding.txtDesc.setOnClickListener(this)
        binding.imgDetailArrow.animRotate(0f)
    }

    fun onCreateSouce(mid: Long) {
        requestMovieInfo(mid)
    }

    private fun requestMovieInfo(mid: Long) {
        playerActivity.lifecycleScope.launch(Dispatchers.IO) {
            val source = model.getMovieDetailById(mid)
            withContext(Dispatchers.Main) {
                when (source) {
                    is Source.Success -> {
                        dataDetail = source.requireData()
                        detail = dataDetail!!.detail
                        val nameArray = listOf<String>()
                        val desc = detail!!.desc
                        val listArray = dataDetail!!.detail.performer
                        val nameList = nameArray.toMutableList()
                        val items: ArrayList<ActorItem> = ArrayList()
                        if (listArray!!.isNullOrEmpty() || listArray.isEmpty()) {
                            //Msg.toast("暂无数据")
                            binding.includeMovieInfo.txtEmpty.NoData.visibility = View.VISIBLE
                            binding.includeMovieInfo.txtDetail.visibility = View.GONE
                            binding.includeMovieInfo.txtLabel.visibility = View.GONE
                            return@withContext
                        }
                        for (i in listArray.indices) {
                            binding.includeMovieInfo.txtEmpty.NoData.visibility = View.GONE
                            binding.includeMovieInfo.txtDetail.visibility = View.VISIBLE
                            binding.includeMovieInfo.txtLabel.visibility = View.VISIBLE
                            val former: ApiMovieDetailById.Performer = listArray[i]
                            val actorItem = ActorItem()
                            actorItem.getData(former)
                            nameList.add(former.name)
                            binding.includeMovieInfo.txtDetail.text = desc
                            binding.includeMovieInfo.txtLabel.text = nameList
                                    .joinToString("\u3000")
                            items.add(actorItem)
                            adapter.replaceAll(items)
                        }
                    }
                    else -> Msg.handleSourceException(source.requireError())
                }.exhaustive
            }
        }
    }

    @SingleClick
    override fun onClick(v: View) {
        when (v.id) {
            R.id.txt_desc -> {
                if (isAnimating) return
                isAnimating = true
                if (binding.includeMovieInfo.layoutMovieInfo.isExpanded) {
                    binding.imgDetailArrow.animRotate(0f)
                } else {
                    binding.imgDetailArrow.animRotate(180f)
                }
                if (binding.layoutLike.visibility == View.VISIBLE) {
                    binding.layoutLike.visibility = View.GONE
                } else {
                    binding.layoutLike.visibility = View.VISIBLE
                }
                binding.includeMovieInfo.layoutMovieInfo.toggle()
            }
            R.id.img_profile -> {
//                val holder = getHolder(v)
//                var item = holder.item<ActorItem>()
//                Msg.toast("点击了   " + item.data!!.id)
            }
        }
    }

    override fun load(view: ImageView, holder: AdapterViewHolder) {
        GlideApp.with(AndroidX.myApp)
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