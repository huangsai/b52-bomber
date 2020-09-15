package com.mobile.app.bomber.movie.top.like

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.app.bomber.common.base.MyBaseActivity
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.databinding.MovieActivityTopLikeBinding
import com.mobile.app.bomber.movie.top.TopTitlePresenter
import com.pacific.adapter.RecyclerAdapter
import com.pacific.adapter.RecyclerItem
import java.util.*

/**
 * 精彩二级页面-猜你喜欢
 */
class TopLikeActivity : MyBaseActivity(), View.OnClickListener {

    private lateinit var binding: MovieActivityTopLikeBinding
    private val adapter = RecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MovieActivityTopLikeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.layoutToolbar.toolbar.title = ""
        setSupportActionBar(binding.layoutToolbar.toolbar)
        binding.layoutToolbar.toolbar.setNavigationIcon(R.drawable.jq_fanhui)
        binding.layoutToolbar.toolbar.setNavigationOnClickListener(this)
        binding.layoutToolbar.txtToolbarTitle.text = getString(R.string.movie_text_top_like_label)
        initRecycler()
    }

    private fun initRecycler() {
        binding.recycler.setHasFixedSize(true)
        binding.recycler.layoutManager = LinearLayoutManager(this)
        binding.recycler.adapter = adapter

        val list: MutableList<RecyclerItem> = ArrayList()
        list.add(TopTitlePresenter(getString(R.string.movie_text_visit_history_label)))
        list.add(HistoryVisitPresenter(this))
        list.add(TopTitlePresenter(getString(R.string.movie_text_top_like_label)))
        list.add(LikeListPresenter(this))
        adapter.addAll(list)
    }

    @SingleClick
    override fun onClick(v: View) {
        finish()
    }
}