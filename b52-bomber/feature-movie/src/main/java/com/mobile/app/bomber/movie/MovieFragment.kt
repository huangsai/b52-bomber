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
import com.mobile.app.bomber.runner.features.ApiMovieFragment
import com.mobile.guava.android.mvvm.newStartActivity
import com.mobile.guava.android.mvvm.showDialogFragment
import com.mobile.guava.android.ui.view.recyclerview.cancelRefreshing
import com.mobile.guava.jvm.coroutines.Bus
import com.mobile.guava.jvm.domain.Source
import com.mobile.guava.jvm.extension.exhaustive
import com.pacific.adapter.RecyclerAdapter
import com.pacific.adapter.RecyclerItem
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
    private var bannerPresenter: BannerPresenter? = null
    private var movieVpPresenter: MovieVpPresenter? = null
    private val adapter = RecyclerAdapter()

    private val labels = ArrayList<String>()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = MovieFragmentMovieBinding.inflate(inflater, container, false)
        binding.menu.setOnClickListener(this)
        binding.searchToolbar.setOnClickListener(this)
        binding.swipeRefresh.setOnRefreshListener(this)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.layoutManager = layoutManager
        binding.recycler.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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
                        val list: MutableList<RecyclerItem> = ArrayList()
                        bannerPresenter = BannerPresenter(this@MovieFragment, model)
                        movieVpPresenter = MovieVpPresenter(this@MovieFragment, labels, binding)
                        list.add(bannerPresenter!!)
                        list.add(movieVpPresenter!!)
                        adapter.addAll(list)
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
        bannerPresenter?.onRefresh()
        Bus.offer(BUS_MOVIE_REFRESH)
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
        binding.recycler.adapter = null
        _binding = null
    }


}