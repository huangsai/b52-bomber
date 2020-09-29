package com.mobile.app.bomber.movie.common

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.common.base.MyBaseFragment
import com.mobile.app.bomber.common.base.RecyclerAdapterEmpty
import com.mobile.app.bomber.movie.MovieViewModel
import com.mobile.app.bomber.movie.MovieX
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.databinding.MovieFragmentCommonListBinding
import com.mobile.ext.glide.GlideApp
import com.mobile.guava.android.ui.view.recyclerview.LinearItemDecoration
import com.pacific.adapter.AdapterImageLoader
import com.pacific.adapter.AdapterViewHolder

class CommonListFragment : MyBaseFragment(), AdapterImageLoader, View.OnClickListener {

    private val model: MovieViewModel by viewModels { MovieX.component.viewModelFactory() }

    private var _binding: MovieFragmentCommonListBinding? = null
    private val binding get() = _binding!!
    private val adapter = RecyclerAdapterEmpty()

    companion object {
        @JvmStatic
        fun newInstance(position: Int): CommonListFragment = CommonListFragment().apply {
            arguments = Bundle().apply {
                putInt("position", position)
            }
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = MovieFragmentCommonListBinding.inflate(inflater, container, false)
        val itemDecoration = LinearItemDecoration.builder(requireContext())
                .color(android.R.color.transparent, R.dimen.size_28dp)
                .build()
        val layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.VERTICAL,
                false)
        binding.recycler.layoutManager = layoutManager
        binding.recycler.addItemDecoration(itemDecoration)
        adapter.setEmptyView(binding.txtEmpty, binding.recycler)
        adapter.imageLoader = this
        adapter.onClickListener = this
        binding.recycler.adapter = adapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        load()
    }

    private fun load() {
    }

    override fun load(imageView: ImageView, holder: AdapterViewHolder) {
        GlideApp.with(requireContext())
                .load("")
                .placeholder(R.drawable.movie_default_cover)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView)
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    override fun onClick(v: View) {
        Msg.toast("点击了视频")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recycler.layoutManager = null
        binding.recycler.adapter = null
        _binding = null
    }
}