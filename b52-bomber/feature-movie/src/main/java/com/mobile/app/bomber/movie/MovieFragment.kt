package com.mobile.app.bomber.movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.common.base.MyBaseFragment
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.app.bomber.movie.MovieX.BUS_MOVIE_REFRESH
import com.mobile.app.bomber.movie.databinding.MovieFragmentMovieBinding
import com.mobile.app.bomber.movie.search.SearchActivity
import com.mobile.app.bomber.movie.top.BannerPresenter
import com.mobile.app.bomber.movie.top.MovieContentPresenter
import com.mobile.app.bomber.runner.features.ApiMovieFragment
import com.mobile.guava.android.mvvm.newStartActivity
import com.mobile.guava.android.mvvm.showDialogFragment
import com.mobile.guava.android.ui.view.recyclerview.cancelRefreshing
import com.mobile.guava.jvm.coroutines.Bus
import com.mobile.guava.jvm.domain.Source
import com.mobile.guava.jvm.extension.exhaustive
import com.pacific.adapter.RecyclerAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MovieFragment : MyBaseFragment(), ApiMovieFragment, View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private val model: MovieViewModel by viewModels { MovieX.component.viewModelFactory() }

    private var _binding: MovieFragmentMovieBinding? = null
    private val binding: MovieFragmentMovieBinding get() = _binding!!

    private val adapter = RecyclerAdapter()
    private val tabLabels = ArrayList<String>()
    private lateinit var movieContentPresenter: MovieContentPresenter
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
        binding.recycler.setItemViewCacheSize(5)
        binding.recycler.layoutManager = LinearLayoutManager(requireActivity())
        binding.recycler.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadTabLabels()
    }

    private fun loadTabLabels() {
        lifecycleScope.launch(Dispatchers.IO) {
            val source = model.getLabel()
            source.dataOrNull()?.let {
                tabLabels.add("精彩")
                tabLabels.addAll(it)
            }
            withContext(Dispatchers.Main) {
                when (source) {
                    is Source.Success -> {
                        movieContentPresenter = MovieContentPresenter(this@MovieFragment, tabLabels, binding)
                        bannerPresenter = BannerPresenter(this@MovieFragment, model)
                        adapter.add(bannerPresenter)
                        adapter.add(movieContentPresenter)
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
        bannerPresenter.onRefresh()
        binding.swipeRefresh.cancelRefreshing(1000)
        Bus.offer(BUS_MOVIE_REFRESH)
    }

    @SingleClick
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.menu -> {
                showDialogFragment(MovieLabelDialogFragment.newInstance(tabLabels))
            }
            R.id.search_toolbar -> {
                newStartActivity(SearchActivity::class.java)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {

        @JvmStatic
        fun newInstance(position: Int): MovieFragment = MovieFragment().apply {
            arguments = Bundle().apply {
                putInt("position", position)
            }
        }
    }
}