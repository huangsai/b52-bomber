package com.mobile.app.bomber.tik.home

import android.app.Activity
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.mobile.guava.android.ui.view.recyclerview.enforceSingleScrollDirection
import com.mobile.guava.android.ui.view.viewpager.recyclerView
import com.mobile.app.bomber.tik.R
import com.mobile.app.bomber.tik.base.AppRouterUtils
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.app.bomber.runner.base.PrefsManager
import com.mobile.app.bomber.tik.base.requireLogin
import com.mobile.guava.android.mvvm.newStartActivity
import com.mobile.app.bomber.tik.category.CategoryActivity
import com.mobile.app.bomber.tik.databinding.FragmentHomeBinding
import com.mobile.app.bomber.tik.login.LoginActivity
import com.mobile.app.bomber.tik.search.SearchActivity
import com.mobile.app.bomber.tik.video.VideoRecordActivity
import com.wanglu.lib.WPopup
import com.wanglu.lib.WPopupModel
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : TopMainFragment(), View.OnClickListener {

    private val tabTitles = arrayOf("关注", "推荐")
    private val data = mutableListOf(WPopupModel("分类"), WPopupModel("同城"))

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var mediator: TabLayoutMediator
    private lateinit var adapter: MyAdapter

    private var menu_pop: WPopup? = null

    private var currentPosition = -1
    private var ignoreOnPageChangeCallback = false
    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            if (ignoreOnPageChangeCallback) {
                ignoreOnPageChangeCallback = false
                return
            }
            if (position == 0) {
                requireActivity().requireLogin(ActivityResultCallback {
                    if (it.resultCode == Activity.RESULT_OK) {
                        currentPosition = position
                    } else {
                        ignoreOnPageChangeCallback = true
                        binding.viewPager.setCurrentItem(currentPosition, false)
                    }
                })
            } else {
                currentPosition = position
            }
        }
    }

    val model: HomeViewModel by viewModels { AppRouterUtils.viewModelFactory() }
    var currentFragment: TopHomeFragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            position = it.getInt("position")
        }
        adapter = MyAdapter(this)


    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        binding.imgSearch.setOnClickListener(this)
        binding.imgCamera.setOnClickListener(this)
        binding.imgDown.setOnClickListener(this)
        binding.viewPager.recyclerView.enforceSingleScrollDirection()
        binding.viewPager.offscreenPageLimit = 2
        binding.viewPager.adapter = adapter
        initPop()


        currentPosition = 1
        binding.viewPager.setCurrentItem(currentPosition, false)

        binding.viewPager.registerOnPageChangeCallback(onPageChangeCallback)
        mediator = TabLayoutMediator(binding.layoutTab, binding.viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }
        mediator.attach()
        return binding.root
    }

    private fun initPop() {

        menu_pop = WPopup.Builder(pActivity)
                .setData(data)
                .setTextColor(Color.WHITE)
                .setIsDim(true)
                .setPopupBgColor(Color.parseColor("#3498db"))
                .setDividerColor(Color.parseColor("#95a5a6"))
                .setOnItemClickListener(object : WPopup.Builder.OnItemClickListener {
                    override fun onItemClick(view: View, position: Int) {
                        if (position == 0) {
                            newStartActivity(CategoryActivity::class.java)
                        } else {
                            newStartActivity(NearByActivity::class.java)
                        }
                    }
                })
                .create()
    }

    @SingleClick
    override fun onClick(v: View?) {

        when (v!!.id) {
            R.id.img_search -> newStartActivity(SearchActivity::class.java)
            R.id.img_camera -> {
                if (PrefsManager.isLogin()) {
                    newStartActivity(VideoRecordActivity::class.java)
                } else {
                    newStartActivity(LoginActivity::class.java)
                }
            }
            R.id.img_down -> {
                menu_pop!!.showAtView(img_down)
            }
        }
    }


    fun selectFragment(position: Int) {
        binding.viewPager.setCurrentItem(position, false)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.viewPager.adapter = null
        binding.viewPager.unregisterOnPageChangeCallback(onPageChangeCallback)
        _binding = null
    }

    companion object {

        @JvmStatic
        fun newInstance(position: Int): HomeFragment = HomeFragment().apply {
            arguments = Bundle().apply {
                putInt("position", position)
            }
        }
    }

    class MyAdapter(fragment: HomeFragment) : FragmentStateAdapter(fragment) {

        override fun getItemCount(): Int = 2

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> FollowingFragment.newInstance(position)
                else -> RecommendFragment.newInstance(position)
            }
        }
    }

}