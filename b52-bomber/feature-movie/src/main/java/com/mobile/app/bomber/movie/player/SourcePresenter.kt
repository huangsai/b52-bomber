package com.mobile.app.bomber.movie.player

import android.graphics.Color
import android.util.Log
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
import master.flame.danmaku.controller.DrawHandler
import master.flame.danmaku.danmaku.model.BaseDanmaku
import master.flame.danmaku.danmaku.model.DanmakuTimer
import master.flame.danmaku.danmaku.model.IDanmakus
import master.flame.danmaku.danmaku.model.IDisplayer
import master.flame.danmaku.danmaku.model.android.DanmakuContext
import master.flame.danmaku.danmaku.model.android.Danmakus
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

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
    private lateinit var mParser: BaseDanmakuParser
    private var mContext: DanmakuContext? = null
    var index = 1
    var timer: Timer = Timer()
    var task: TimerTask = object : TimerTask() {
        override fun run() {
            try {
                detail?.marquee?.let {
                    addDanmaku(it)
                    if (index >= it.num) {
                        timer.cancel()
                    }
                    index++
                }

            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

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
        initDanmaku(mid)
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
                        detail?.marquee?.let {
                            timer.schedule(task, 0, it.interval * 1000L)
                        }
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

    private fun initDanmaku(mid: Long) {
        mParser = getDefaultDanmakuParser()
        // 设置最大显示行数
        val maxLinesPair = HashMap<Int, Int>()
        maxLinesPair[BaseDanmaku.TYPE_SCROLL_RL] = 5 // 滚动弹幕最大显示5行
        // 设置是否禁止重叠
        val overlappingEnablePair = HashMap<Int, Boolean>()
        overlappingEnablePair[BaseDanmaku.TYPE_SCROLL_RL] = true
        overlappingEnablePair[BaseDanmaku.TYPE_FIX_TOP] = true

        mContext = DanmakuContext.create()
        mContext!!.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3f)
                .setDuplicateMergingEnabled(false).setScrollSpeedFactor(1.2f).setScaleTextSize(1.2f)
                //        .setCacheStuffer(new BackgroundCacheStuffer())  // 绘制背景使用BackgroundCacheStuffer
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair).setDanmakuMargin(40)

        binding.danmaku.setCallback(object : DrawHandler.Callback {
            override fun updateTimer(timer: DanmakuTimer?) {}
            override fun drawingFinished() {}
            override fun danmakuShown(danmaku: BaseDanmaku) {
//                    Log.d("DFM", "danmakuShown(): text=" + danmaku.text);
            }

            override fun prepared() {
                binding.danmaku.start()
                requestMovieInfo(mid)
            }
        })

        binding.danmaku.prepare(mParser, mContext)
        binding.danmaku.enableDanmakuDrawingCache(true)
    }

    private fun addDanmaku(marquee: ApiMovieDetailById.Marquee) {

        //创建一个弹幕对象，这里后面的属性是设置滚动方向的！
        val danmaku = mContext!!.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL) ?: return
        //弹幕显示的文字
        danmaku.text = marquee.content
        //设置相应的边距，这个设置的是四周的边距
        danmaku.padding = 5
        // 可能会被各种过滤器过滤并隐藏显示，若果是本机发送的弹幕，建议设置成1；
        danmaku.priority = 1
        //是否是直播弹幕
        danmaku.isLive = false
        danmaku.time = binding.danmaku.currentTime + 1200
        //设置文字大小
        danmaku.textSize = 40f
        //设置文字颜色
        danmaku.textColor = Color.WHITE
        //设置阴影的颜色
        danmaku.textShadowColor = Color.WHITE
        // danmaku.underlineColor = Color.GREEN;
        //添加这条弹幕，也就相当于发送
        binding.danmaku.addDanmaku(danmaku)
    }

    private fun getDefaultDanmakuParser(): BaseDanmakuParser {
        return object : BaseDanmakuParser() {
            override fun parse(): IDanmakus {
                return Danmakus()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.cancel()
    }

}