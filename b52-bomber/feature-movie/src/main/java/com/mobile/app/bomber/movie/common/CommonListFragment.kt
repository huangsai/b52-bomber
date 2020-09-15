package com.mobile.app.bomber.movie.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobile.app.bomber.common.base.MyBaseFragment
import com.mobile.app.bomber.movie.MovieLib
import com.mobile.app.bomber.movie.MovieViewModel
import com.mobile.app.bomber.movie.databinding.MovieFragmentCommonListBinding
import com.mobile.app.bomber.movie.top.BannerPresenter
import com.pacific.adapter.RecyclerAdapter
import com.pacific.adapter.RecyclerItem
import java.util.*

class CommonListFragment : MyBaseFragment() {

    private val model: MovieViewModel by viewModels { MovieLib.component.viewModelFactory() }
    private var _binding: MovieFragmentCommonListBinding? = null
    private val binding get() = _binding!!
    private val adapter = RecyclerAdapter()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = MovieFragmentCommonListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycler.setHasFixedSize(true)
        binding.recycler.layoutManager = LinearLayoutManager(requireActivity())
        binding.recycler.adapter = adapter
        load()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recycler.layoutManager = null
        binding.recycler.adapter = null
        _binding = null
    }

    private fun load() {
        val list: MutableList<RecyclerItem> = ArrayList()
        list.add(BannerPresenter(this))
        list.add(CommonListMoviePresenter(requireContext()))
        adapter.addAll(list)
    }

    companion object {
        @JvmStatic
        fun newInstance(position: Int): CommonListFragment = CommonListFragment().apply {
            arguments = Bundle().apply {
                putInt("position", position)
            }
        }
    }
}