package com.mobile.app.bomber.movie.top

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.ViewPager2
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.common.base.MyBaseFragment
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.app.bomber.movie.MovieViewModel
import com.mobile.app.bomber.movie.MovieX
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.databinding.MovieFragmentTopListBinding
import com.mobile.app.bomber.movie.top.lastupdate.TopLastupdateActivity
import com.mobile.app.bomber.movie.top.like.TopLikeActivity
import com.mobile.app.bomber.movie.top.recommend.TopRecommendActivity
import com.mobile.guava.android.mvvm.newStartActivity
import com.mobile.guava.android.ui.view.recyclerview.cancelRefreshing
import com.mobile.guava.jvm.domain.Source
import com.mobile.guava.jvm.extension.exhaustive
import com.pacific.adapter.AdapterUtils
import com.pacific.adapter.RecyclerAdapter
import com.pacific.adapter.RecyclerItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class TopListFragment : MyBaseFragment(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private val model: MovieViewModel by viewModels { MovieX.component.viewModelFactory() }

    private var _binding: MovieFragmentTopListBinding? = null
    private val binding get() = _binding!!

    private val adapter = RecyclerAdapter()
    private lateinit var bannerPresenter: BannerPresenter

    private lateinit var listNearPresenter: TopListNearPresenter

    private lateinit var listLikePresenter: TopListLikePresenter

    private lateinit var listRecommendPresenter: TopListRecommendPresenter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = MovieFragmentTopListBinding.inflate(inflater, container, false)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.setItemViewCacheSize(10)
        binding.recycler.layoutManager = layoutManager
        binding.recycler.adapter = adapter
        adapter.onClickListener = this
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
        val list: MutableList<RecyclerItem> = ArrayList()
        list.add(MovieSearchPresenter(this))
        bannerPresenter = BannerPresenter(this, model)

        listNearPresenter = TopListNearPresenter(this, model)
        listLikePresenter = TopListLikePresenter(this, model)
        listRecommendPresenter = TopListRecommendPresenter(this, model)
        list.add(bannerPresenter)

        lifecycleScope.launch(Dispatchers.IO) {
            val source = model.getBanner()
            withContext(Dispatchers.Main) {
                when (source) {
                    is Source.Success -> {
                        var listData = source.requireData()
                        if (listData.isEmpty() || listData.size < 1) {
                            list.remove(bannerPresenter)
                            adapter.remove(bannerPresenter)
                        } else {

                        }
                    }
                    is Source.Error -> {
                        Msg.handleSourceException(source.requireError())
                    }
                }.exhaustive
            }
        }
        list.add(TopTitlePresenter("${getString(R.string.movie_text_top_near_label)}>"))
        list.add(listNearPresenter)
        list.add(TopTitlePresenter("${getString(R.string.movie_text_top_like_label)}>"))
        list.add(listLikePresenter)
        list.add(TopTitlePresenter("${getString(R.string.movie_text_top_recommend_label)}>"))
        list.add(listRecommendPresenter)

        adapter.addAll(list)
    }

    @SingleClick
    override fun onClick(v: View) {
        when (v.id) {
            R.id.item_title -> {
                val pos = AdapterUtils.getHolder(v).bindingAdapterPosition
                var presenter: TopTitlePresenter = adapter.get<RecyclerItem>(pos) as TopTitlePresenter
                when {
                    presenter.name.contains("最近更新") -> {
                        newStartActivity(TopLastupdateActivity::class.java)
                    }
                    presenter.name.contains("猜你喜欢") -> {
                        newStartActivity(TopLikeActivity::class.java)
                    }
                    presenter.name.contains("为你推荐") -> {
                        newStartActivity(TopRecommendActivity::class.java)
                    }
                }
            }
        }
    }

    override fun onRefresh() {
        binding.swipeRefresh.cancelRefreshing(1000)
        bannerPresenter.onRefresh()
        listNearPresenter.onRefresh()
        listLikePresenter.onRefresh()
        listRecommendPresenter.onRefresh()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recycler.layoutManager = null
        binding.recycler.adapter = null
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance(position: Int): TopListFragment = TopListFragment().apply {
            arguments = Bundle().apply {
                putInt("position", position)
            }
        }
    }

}