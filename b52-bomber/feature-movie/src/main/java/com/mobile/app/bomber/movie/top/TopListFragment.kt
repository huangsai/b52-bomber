package com.mobile.app.bomber.movie.top

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.app.bomber.common.base.MyBaseFragment
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.app.bomber.movie.MovieViewModel
import com.mobile.app.bomber.movie.MovieX
import com.mobile.app.bomber.movie.MovieX.BUS_MOVIE_REFRESH
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.databinding.MovieFragmentTopListBinding
import com.mobile.app.bomber.movie.top.like.TopLikeActivity
import com.mobile.app.bomber.movie.top.recommend.TopRecommendActivity
import com.mobile.guava.android.mvvm.newStartActivity
import com.pacific.adapter.AdapterUtils
import com.pacific.adapter.RecyclerAdapter
import com.pacific.adapter.RecyclerItem
import java.util.*

class TopListFragment : MyBaseFragment(), View.OnClickListener {

    private val model: MovieViewModel by viewModels { MovieX.component.viewModelFactory() }

    private var _binding: MovieFragmentTopListBinding? = null
    private val binding get() = _binding!!

    private val adapter = RecyclerAdapter()

    private lateinit var listLikePresenter: TopListLikePresenter
    private lateinit var listRecommendPresenter: TopListRecommendPresenter

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = MovieFragmentTopListBinding.inflate(inflater, container, false)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.layoutManager = layoutManager
        binding.recycler.adapter = adapter
        adapter.onClickListener = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        load()
    }

    private fun load() {
        val list: MutableList<RecyclerItem> = ArrayList()
        list.add(TopTitlePresenter("${getString(R.string.movie_text_top_like_label)}>"))
        listLikePresenter = TopListLikePresenter(requireContext())
        list.add(listLikePresenter)
        list.add(TopTitlePresenter("${getString(R.string.movie_text_top_recommend_label)}>"))
        listRecommendPresenter = TopListRecommendPresenter(requireContext())
        list.add(listRecommendPresenter)
        adapter.addAll(list)
    }

    override fun onBusEvent(event: Pair<Int, Any>) {
        if (event.first == BUS_MOVIE_REFRESH) {
            listLikePresenter.onRefresh()
            listRecommendPresenter.onRefresh()
        }
    }

    @SingleClick
    override fun onClick(v: View) {
        when (v.id) {
            R.id.item_title -> {
                val pos = AdapterUtils.getHolder(v).bindingAdapterPosition
                if (pos == 1) {
                    newStartActivity(TopLikeActivity::class.java)
                } else if (pos == 3) {
                    newStartActivity(TopRecommendActivity::class.java)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recycler.layoutManager = null
        binding.recycler.adapter = null
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
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