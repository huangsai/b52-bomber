package com.mobile.app.bomber.tik.home

import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.mobile.app.bomber.data.http.entities.ApiVideo
import com.mobile.guava.android.ui.view.recyclerview.EndlessRecyclerViewScrollListener
import com.mobile.guava.android.ui.view.recyclerview.TestedGridItemDecoration
import com.mobile.guava.android.ui.view.recyclerview.disableDefaultItemAnimator
import com.mobile.guava.jvm.domain.Source
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.pacific.adapter.AdapterImageLoader
import com.pacific.adapter.AdapterViewHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.mobile.app.bomber.tik.R
import com.mobile.ext.glide.GlideApp
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.common.base.RecyclerViewFragment
import com.mobile.app.bomber.tik.base.decodeImgUrl
import com.mobile.app.bomber.tik.base.loadProfile
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.app.bomber.tik.base.AppRouterUtils
import com.mobile.app.bomber.tik.databinding.FragmentNearbyBinding
import com.mobile.app.bomber.tik.home.items.NearbyVideoItem
import com.pacific.adapter.AdapterUtils

class NearbyFragment : RecyclerViewFragment(), AdapterImageLoader, View.OnClickListener {

    private var _binding: FragmentNearbyBinding? = null
    private val binding get() = _binding!!
    private val model: HomeViewModel by viewModels { AppRouterUtils.viewModelFactory() }

    private val fixStaggered = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            (binding.recycler.layoutManager as StaggeredGridLayoutManager).invalidateSpanAssignments()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter.imageLoader = this
        adapter.onClickListener = this
        adapter.onDataSetChanged = this
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentNearbyBinding.inflate(inflater, container, false)
        binding.layoutRefresh.setOnRefreshListener(this)
        binding.recycler.addItemDecoration(TestedGridItemDecoration(requireActivity(), R.dimen.size_1dp))
        binding.recycler.layoutManager = StaggeredGridLayoutManager(
                2, StaggeredGridLayoutManager.VERTICAL
        ).apply {
            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_NONE
        }.also {
            endless = EndlessRecyclerViewScrollListener(it) { _, _ ->
                if (pager.isAvailable) {
                    binding.progress.visibility = View.VISIBLE
                    load()
                }
            }
            binding.recycler.disableDefaultItemAnimator()
            binding.recycler.addOnScrollListener(endless)
            binding.recycler.addOnScrollListener(fixStaggered)
        }
        binding.recycler.adapter = adapter
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        loadIfEmpty()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recycler.removeOnScrollListener(endless)
        binding.recycler.removeOnScrollListener(fixStaggered)
        binding.recycler.adapter = null
        _binding = null
    }
    //重写父类方法，加载背景图
    override fun load(view: ImageView, holder: AdapterViewHolder) {
        val data: ApiVideo.Video = holder.item<NearbyVideoItem>().data
        when (view.id) {
            R.id.img_cover -> {
                GlideApp.with(this)
                        .load(decodeImgUrl(data.coverImageUrl))
                        .placeholder(R.drawable.nearby_absent)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .thumbnail(0.25f)
                        .into(view)
            }
            R.id.img_profile -> {
                this.loadProfile(data.profile, view)
            }
        }
    }

    @SingleClick
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.item_nearby_video -> {
                PlayListActivity.start(
                        requireActivity(),
                        extractAdapter(),
                        AdapterUtils.getHolder(v).bindingAdapterPosition
                )
            }
        }
    }

    override fun apply(count: Int) {
        if (count == 0) {
            binding.txtEmpty.visibility = View.VISIBLE
        } else {
            binding.txtEmpty.visibility = View.INVISIBLE
        }
    }
   //重写load 方法。加载接口数据
    override fun load() {
        if (!pager.isAvailable) return

        lifecycleScope.launch(Dispatchers.Default) {
            val location: Location? = LocationLiveData.value
            val source = model.nearbyVideo(
                    pager,
                    location?.latitude ?: 0.00,
                    location?.longitude ?: 0.00
            )
            val data = source.dataOrNull().orEmpty().map {
                NearbyVideoItem(it)
            }
            withContext(Dispatchers.Main) {
                binding.recycler.stopScroll()
                when (source) {
                    is Source.Success -> {
                        if (data.isNotEmpty()) {
                            if (pager.isFirstPage(2)) {
                                adapter.replaceAll(data)
                            } else {
                                adapter.addAll(data)
                            }
                        }
                    }
                    is Source.Error -> Msg.handleSourceException(source.requireError())
                }
                falseRefreshing()
            }
        }
    }
    // 自动刷新
    override fun autoOnRefresh() {
        binding.layoutRefresh.isRefreshing = true
        onRefresh()
    }
    //停止刷新
    override fun falseRefreshing() {
        binding.txtEmpty.postDelayed({
            binding.layoutRefresh.isRefreshing = false
            binding.progress.visibility = View.INVISIBLE
        }, 500)
    }

    private fun extractAdapter(): List<ApiVideo.Video> {
        return adapter.getAll()
                .filterIsInstance<NearbyVideoItem>()
                .map { it.data }
    }

    companion object {

        @JvmStatic
        fun newInstance(index: Int): NearbyFragment = NearbyFragment().apply {
            arguments = Bundle().apply {
                putInt("position", index)
            }
        }
    }
}