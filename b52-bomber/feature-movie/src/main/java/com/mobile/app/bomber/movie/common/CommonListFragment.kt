package com.mobile.app.bomber.movie.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mobile.app.bomber.common.base.Msg.handleSourceException
import com.mobile.app.bomber.common.base.MyBaseFragment
import com.mobile.app.bomber.common.base.RecyclerAdapterEmpty
import com.mobile.app.bomber.data.http.entities.Pager
import com.mobile.app.bomber.movie.MovieViewModel
import com.mobile.app.bomber.movie.MovieX
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.common.items.CommonMovieItem
import com.mobile.app.bomber.movie.databinding.MovieFragmentCommonListBinding
import com.mobile.app.bomber.movie.player.PlayerActivity
import com.mobile.ext.glide.GlideApp
import com.mobile.guava.android.ui.view.recyclerview.EndlessRecyclerViewScrollListener
import com.mobile.guava.android.ui.view.recyclerview.LinearItemDecoration
import com.mobile.guava.android.ui.view.recyclerview.cancelRefreshing
import com.mobile.guava.jvm.domain.Source
import com.mobile.guava.jvm.extension.exhaustive
import com.pacific.adapter.AdapterImageLoader
import com.pacific.adapter.AdapterUtils
import com.pacific.adapter.AdapterViewHolder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CommonListFragment : MyBaseFragment(), AdapterImageLoader, View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private val model: MovieViewModel by viewModels { MovieX.component.viewModelFactory() }

    private var _binding: MovieFragmentCommonListBinding? = null
    private val binding get() = _binding!!
    private val adapter = RecyclerAdapterEmpty()
    private lateinit var endless: EndlessRecyclerViewScrollListener
    private lateinit var label: String

    private val pager = Pager()

    companion object {
        @JvmStatic
        fun newInstance(position: Int, label: String): CommonListFragment = CommonListFragment().apply {
            arguments = Bundle().apply {
                putInt("position", position)
                putString("label", label)
            }
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        label = arguments?.getString("label").toString()
        _binding = MovieFragmentCommonListBinding.inflate(inflater, container, false)
        val itemDecoration = LinearItemDecoration.builder(requireContext())
                .color(android.R.color.transparent, R.dimen.size_12dp)
                .build()
        binding.recycler.layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false)
                .also {
                    endless = EndlessRecyclerViewScrollListener(it) { _, _ ->
                        if (pager.isAvailable) {
                            load()
                        }
                    }
                    binding.recycler.addOnScrollListener(endless)
                }
        binding.recycler.addItemDecoration(itemDecoration)
        adapter.imageLoader = this
        adapter.onClickListener = this
        adapter.setEmptyView(binding.txtEmpty, binding.recycler)
        binding.recycler.adapter = adapter
        binding.swipeRefresh.setOnRefreshListener(this)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (onResumeCount == 1) {
            load()
        }
    }

    private fun load() {
        if (!pager.isAvailable) return
        lifecycleScope.launch(Dispatchers.IO) {
            val source = model.getMovieListByLabel(pager, label)
            val items = source.dataOrNull().orEmpty().map { CommonMovieItem(it) }
            withContext(Dispatchers.Main) {
                when (source) {
                    is Source.Success -> {
                        if (pager.isFirstPage(2)) {
                            adapter.replaceAll(items)
                        } else {
                            adapter.addAll(items)
                        }
                    }
                    is Source.Error -> handleSourceException(source.requireError())
                }.exhaustive
            }
        }
    }

    override fun load(view: ImageView, holder: AdapterViewHolder) {
        val data = AdapterUtils.getHolder(view).item<CommonMovieItem>().data
        GlideApp.with(requireContext())
                .load(data.cover)
                .placeholder(R.drawable.movie_default_cover)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(view)
    }

    override fun onRefresh() {
        binding.swipeRefresh.cancelRefreshing(1000)
        pager.reset()
        endless.reset()
        load()
    }

    override fun onClick(v: View) {
        val data = AdapterUtils.getHolder(v).item<CommonMovieItem>().data
        PlayerActivity.start(requireActivity(), data.movieId.toLong())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recycler.removeOnScrollListener(endless)
        binding.recycler.layoutManager = null
        binding.recycler.adapter = null
        _binding = null
    }

}