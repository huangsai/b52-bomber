package com.mobile.app.bomber.movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.mobile.app.bomber.common.base.MyBaseFragment
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.app.bomber.movie.common.CommonListFragment
import com.mobile.app.bomber.movie.databinding.MovieFragmentMovieBinding
import com.mobile.app.bomber.movie.search.SearchActivity
import com.mobile.app.bomber.movie.top.TopListFragment
import com.mobile.app.bomber.runner.features.ApiMovieFragment
import com.mobile.guava.android.mvvm.newStartActivity
import com.mobile.guava.android.mvvm.showDialogFragment

class MovieFragment : MyBaseFragment(), ApiMovieFragment, View.OnClickListener {

    companion object {
        @JvmStatic
        fun newInstance(position: Int): MovieFragment = MovieFragment().apply {
            arguments = Bundle().apply {
                putInt("position", position)
            }
        }
    }

    private val model: MovieViewModel by viewModels { MovieLib.component.viewModelFactory() }

    private var _binding: MovieFragmentMovieBinding? = null
    private val binding: MovieFragmentMovieBinding get() = _binding!!

    private val labels = ArrayList<String>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = MovieFragmentMovieBinding.inflate(inflater, container, false)
        binding.menu.setOnClickListener(this)
        binding.searchToolbar.setOnClickListener(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initTab()
    }

    private fun initTab() {
        labels.add("精彩")
        labels.add("自制网剧")
        labels.add("名导大片")
        labels.add("欧美大片")
        labels.add("岛国AV")
        labels.add("丝袜诱惑")
        labels.add("人兽结合")
        labels.add("精彩港剧")
        labels.add("精彩韩剧")
        binding.viewPager.adapter = MyAdapter(requireActivity(), labels.size)
        binding.viewPager.offscreenPageLimit = 2
        val tabLayoutMediator = TabLayoutMediator(binding.layoutTab, binding.viewPager, TabConfigurationStrategy { tab, position -> tab.text = labels[position] })
        tabLayoutMediator.attach()
    }

    fun selectTab(pos: Int) {
        binding.layoutTab.selectTab(binding.layoutTab.getTabAt(pos))
    }

    @SingleClick
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.menu -> {
                val labelDialogFragment = MovieLabelDialogFragment.newInstance(labels)
                showDialogFragment(labelDialogFragment)
            }
            R.id.search_toolbar -> {
                newStartActivity(SearchActivity::class.java)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.viewPager.adapter = null
        _binding = null
    }

    private class MyAdapter(
            activity: FragmentActivity,
            private val itemCount: Int
    ) : FragmentStateAdapter(activity) {

        override fun createFragment(position: Int): Fragment {
            return if (position == 0) {
                TopListFragment.newInstance(position)
            } else {
                CommonListFragment.newInstance(position)
            }
        }

        override fun getItemCount(): Int {
            return itemCount
        }
    }
}