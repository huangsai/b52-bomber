package com.mobile.app.bomber.tik.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.snackbar.Snackbar
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.common.base.adapter.BaseFragmentStateAdapter
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.app.bomber.data.http.entities.ApiVideo
import com.mobile.app.bomber.data.repository.is403
import com.mobile.app.bomber.tik.databinding.FragmentRecommendBinding
import com.mobile.app.bomber.tik.login.LoginActivity
import com.mobile.guava.android.mvvm.newStartActivity
import com.mobile.guava.android.ui.view.recyclerview.EndlessRecyclerViewScrollListener
import com.mobile.guava.android.ui.view.viewpager.recyclerView
import com.mobile.guava.jvm.domain.Source
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


abstract class BaseRecommendFragment : TopHomeFragment() {

    private var isFollowingFragment = false

    private var _binding: FragmentRecommendBinding? = null
    protected val binding get() = _binding!!

    protected lateinit var myAdapter: MyAdapter
    protected lateinit var data: List<ApiVideo>

    var currentFragment: PlayFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        myAdapter = MyAdapter(this)
        isFollowingFragment = this is FollowingFragment
        myAdapter.onDataSetChanged = this
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecommendBinding.inflate(inflater, container, false)
        binding.layoutRefresh.setOnRefreshListener(this)
        binding.viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        binding.viewPager.adapter = myAdapter
        binding.viewPager.recyclerView.also {
            endless = EndlessRecyclerViewScrollListener(it.layoutManager!!) { _, _ ->
                if (pager.isAvailable) {
                    binding.progress.visibility = View.VISIBLE
//                    load()
                    val lastVisibleItemPosition: Int? = binding.viewPager.recyclerView.layoutManager?.itemCount
                    //说明是最后一条数据
                    if (lastVisibleItemPosition != null) {
                        if (lastVisibleItemPosition == binding.viewPager.adapter!!.itemCount) {
//                            Msg.toast("加载更多数据")
                            refreshMoreData()
                        }
                    }
                }
            }
            it.addOnScrollListener(endless)
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.viewPager.recyclerView.removeOnScrollListener(endless)
        binding.viewPager.adapter = null
        _binding = null
    }

    override fun loadIfEmpty() {
        if (myAdapter.itemCount == 0) {
            load()
        }
    }

    private fun refreshMoreData() {
//        if (myAdapter.itemCount)
        if (!pager.isAvailable) return
        lifecycleScope.launch(Dispatchers.Default) {
            val source = if (isFollowingFragment) {
                model.videosOfFollow(pager)
            } else {
                model.videosOfCommend(pager)
            }
            withContext(Dispatchers.Main) {
                when (source) {
                    is Source.Success -> {
                        val data = source.requireData()
                        if (data.isNotEmpty()) {
                            myAdapter.addAll(data)
                        }
                    }
                }

            }
            falseRefreshing()
        }
    }

    override fun load() {
        if (!pager.isAvailable) return

        lifecycleScope.launch(Dispatchers.Default) {
            val source = if (isFollowingFragment) {
                model.videosOfFollow(pager)
            } else {
                model.videosOfCommend(pager)
            }
            withContext(Dispatchers.Main) {
                when (source) {
                    is Source.Success -> {
                        val data = source.requireData()
                        if (data.isNotEmpty()) {
                            if (isFollowingFragment) {
                                binding.viewPager.adapter = myAdapter
                                if (pager.isFirstPage(2)) {
                                     myAdapter.replaceAll(data)
                                 } else {
                                      myAdapter.addAll(data)
                                 }
                            }else{
                                //if (pager.isFirstPage(2)) {
                                binding.viewPager.adapter = myAdapter
                                myAdapter.replaceAll(data)
                                binding.txtEmpty.visibility = View.GONE
                            }
//                            if (pager.isFirstPage(2)) {
//                            binding.viewPager.adapter = myAdapter
//                            myAdapter.replaceAll(data)
//                            binding.txtEmpty.visibility = View.GONE
                        } else {
                            myAdapter.clear()
                            binding.txtEmpty.visibility = View.VISIBLE
                        }
                    }
                    is Source.Error -> {
                        if (isFollowingFragment) {
                            if (source.requireError().is403()) {
                                newStartActivity(LoginActivity::class.java);
                            } else {
                                Msg.toast("加载关注数据失败")
                            }
                        } else {
                            Msg.toast("加载推荐数据失败")
                        }
                    }
                }
                falseRefreshing()
            }
        }
    }

    @SingleClick
    override fun onClick(v: View?) {
    }

    override fun apply(count: Int) {
        if (count == 0) {
            binding.txtEmpty.visibility = View.VISIBLE
        } else {
            binding.txtEmpty.visibility = View.INVISIBLE
        }
    }

    override fun autoOnRefresh() {
        binding.layoutRefresh.isRefreshing = true
        onRefresh()
    }

    override fun falseRefreshing() {
        binding.txtEmpty.postDelayed({
            binding.layoutRefresh.isRefreshing = false
            binding.progress.visibility = View.INVISIBLE
        }, 50)
    }

    class MyAdapter(fragment: Fragment) : BaseFragmentStateAdapter<ApiVideo.Video>(fragment) {
        override fun createFragment(position: Int): Fragment {
            return PlayFragment.newInstance(position, data[position])
        }
    }
}