package com.mobile.app.bomber.movie.top.recommend

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mobile.app.bomber.common.base.MyBaseActivity
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.app.bomber.movie.MovieViewModel
import com.mobile.app.bomber.movie.MovieX
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.databinding.MovieActivityTopLikeBinding
import com.mobile.app.bomber.movie.top.TopTitlePresenter
import com.mobile.guava.android.ui.view.recyclerview.cancelRefreshing
import com.pacific.adapter.RecyclerAdapter
import com.pacific.adapter.RecyclerItem
import java.util.*

/**
 * 精彩二级页面-为你推荐
 */
class TopRecommendActivity : MyBaseActivity(), View.OnClickListener, SwipeRefreshLayout.OnRefreshListener {

    private val model: MovieViewModel by viewModels { MovieX.component.viewModelFactory() }

    private lateinit var binding: MovieActivityTopLikeBinding
    private val adapter = RecyclerAdapter()
    private lateinit var recommendListPresenter: RecommendListPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MovieActivityTopLikeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.layoutToolbar.toolbar.title = ""
        setSupportActionBar(binding.layoutToolbar.toolbar)
        binding.layoutToolbar.toolbar.setNavigationIcon(R.drawable.jq_fanhui)
        binding.layoutToolbar.toolbar.setNavigationOnClickListener(this)
        binding.layoutToolbar.txtToolbarTitle.text = getString(R.string.movie_text_top_recommend_label)
        binding.swipeRefresh.setOnRefreshListener(this)
        initRecycler()
    }

    private fun initRecycler() {
        binding.recycler.setHasFixedSize(true)
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = adapter

        val list: MutableList<RecyclerItem> = ArrayList()
//        list.add(TopTitlePresenter(getString(R.string.movie_text_visit_friend_label)))
//        list.add(HistoryFriendPresenter(this))
        list.add(TopTitlePresenter(getString(R.string.movie_text_recommend_best_label)))
        recommendListPresenter = RecommendListPresenter(this, model)
        list.add(recommendListPresenter)
        adapter.addAll(list)
    }

    @SingleClick
    override fun onClick(v: View) {
        finish()
    }

    override fun onRefresh() {
        binding.swipeRefresh.cancelRefreshing(1000)
        recommendListPresenter.onRefresh()
    }
}