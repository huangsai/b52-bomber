package com.mobile.app.bomber.movie.top

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.app.bomber.common.base.MyBaseFragment
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.app.bomber.movie.MovieLib
import com.mobile.app.bomber.movie.MovieViewModel
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

    private val model: MovieViewModel by viewModels { MovieLib.component.viewModelFactory() }

    private var _binding: MovieFragmentTopListBinding? = null
    private val binding get() = _binding!!

    private val adapter = RecyclerAdapter()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = MovieFragmentTopListBinding.inflate(inflater, container, false)
        initRecycler()
        return binding.root
    }

    private fun initRecycler() {
        binding.recycler.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.layoutManager = layoutManager
        binding.recycler.adapter = adapter
        adapter.onClickListener = this
        load()
    }

    private fun load() {
        val list: MutableList<RecyclerItem> = ArrayList()
        list.add(BannerPresenter(this))
        list.add(TopTitlePresenter("${getString(R.string.movie_text_top_like_label)}>"))
        list.add(TopListLikePresenter(requireContext()))
        list.add(TopTitlePresenter("${getString(R.string.movie_text_top_recommend_label)}>"))
        list.add(TopListRecommendPresenter(requireContext()))
        adapter.addAll(list)
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