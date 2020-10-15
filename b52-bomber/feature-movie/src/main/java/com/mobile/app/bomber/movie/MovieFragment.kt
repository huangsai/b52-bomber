package com.mobile.app.bomber.movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.common.base.MyBaseFragment
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.app.bomber.movie.MovieX.BUS_MOVIE_REFRESH
import com.mobile.app.bomber.movie.common.CommonListFragment
import com.mobile.app.bomber.movie.databinding.MovieFragmentMovieBinding
import com.mobile.app.bomber.movie.search.SearchActivity
import com.mobile.app.bomber.movie.top.BannerPresenter
import com.mobile.app.bomber.movie.top.TopListFragment
import com.mobile.app.bomber.runner.features.ApiMovieFragment
import com.mobile.guava.android.mvvm.newStartActivity
import com.mobile.guava.android.mvvm.showDialogFragment
import com.mobile.guava.android.ui.view.recyclerview.cancelRefreshing
import com.mobile.guava.jvm.coroutines.Bus
import com.mobile.guava.jvm.domain.Source
import com.mobile.guava.jvm.extension.exhaustive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieFragment : MyBaseFragment(), ApiMovieFragment, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    companion object {
        @JvmStatic
        fun newInstance(position: Int): MovieFragment = MovieFragment().apply {
            arguments = Bundle().apply {
                putInt("position", position)
            }
        }
    }

    private val model: MovieViewModel by viewModels { MovieX.component.viewModelFactory() }

    private var _binding: MovieFragmentMovieBinding? = null
    private val binding: MovieFragmentMovieBinding get() = _binding!!

    private val labels = ArrayList<String>()
    private lateinit var bannerPresenter: BannerPresenter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = MovieFragmentMovieBinding.inflate(inflater, container, false)
        binding.menu.setOnClickListener(this)
        binding.searchToolbar.setOnClickListener(this)
        binding.swipeRefresh.setOnRefreshListener(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bannerPresenter = BannerPresenter(this, model, binding)
        initTab()
    }

    private fun initTab() {
        labels.add(0, "精彩")
        lifecycleScope.launch(Dispatchers.IO) {
            val source = model.getLabel()
            withContext(Dispatchers.Main) {
                when (source) {
                    is Source.Success -> {
                        labels.addAll(source.data)
                        binding.viewPager.adapter = MyAdapter(requireActivity(), labels)
                        binding.viewPager.offscreenPageLimit = 1
                        val tabLayoutMediator = TabLayoutMediator(binding.layoutTab, binding.viewPager, TabConfigurationStrategy { tab, position -> tab.text = labels[position] })
                        tabLayoutMediator.attach()
                    }
                    is Source.Error -> {
                        Msg.handleSourceException(source.requireError())
                    }
                }.exhaustive
            }
        }
    }

    fun selectTab(pos: Int) {
        binding.layoutTab.selectTab(binding.layoutTab.getTabAt(pos))
    }

    override fun onRefresh() {
        binding.swipeRefresh.cancelRefreshing(1000)
        Bus.offer(BUS_MOVIE_REFRESH)
        bannerPresenter.onRefresh()
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
            private val labels: List<String>
    ) : FragmentStateAdapter(activity) {

        override fun createFragment(position: Int): Fragment {
            return if (position == 0) {
                TopListFragment.newInstance(position)
            } else {
                CommonListFragment.newInstance(position, labels[position])
            }
        }

        override fun getItemCount(): Int {
            return labels.size
        }
    }

}