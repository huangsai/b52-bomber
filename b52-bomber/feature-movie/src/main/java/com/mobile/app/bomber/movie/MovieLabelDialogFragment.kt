package com.mobile.app.bomber.movie

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.recyclerview.widget.GridLayoutManager
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.guava.data.Values
import com.mobile.app.bomber.movie.base.views.MiddleGridItemDecoration
import com.mobile.app.bomber.movie.databinding.MovieDialogLabelBinding
import com.mobile.guava.android.mvvm.BaseAppCompatDialogFragment
import com.pacific.adapter.AdapterUtils
import com.pacific.adapter.RecyclerAdapter

class MovieLabelDialogFragment : BaseAppCompatDialogFragment(), View.OnClickListener {

    companion object {
        @JvmStatic
        fun newInstance(labels: ArrayList<String>) = MovieLabelDialogFragment().apply {
            Values.put("MovieLabelDialogFragment_labels", labels)
        }
    }

    private var _binding: MovieDialogLabelBinding? = null
    private val binding: MovieDialogLabelBinding get() = _binding!!

    private var labels: ArrayList<String>? = null
    private val adapter = RecyclerAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        applyDialogCount = true
        labels = Values.take("MovieLabelDialogFragment_labels")
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)
        _binding = MovieDialogLabelBinding.inflate(layoutInflater)
        binding.layoutToolbar.toolbar.title = ""
        binding.layoutToolbar.toolbar.setNavigationIcon(R.drawable.jq_fanhui)
        binding.layoutToolbar.toolbar.setNavigationOnClickListener(this)
        binding.layoutToolbar.txtToolbarTitle.text = getString(R.string.movie_text_all_label)
        initRecycler()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val items = ArrayList<MovieLabelItem>()
        if (labels != null) {
            for (label: String in labels!!) {
                items.add(MovieLabelItem(label))
            }
        }
        adapter.addAll(items)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
        )
    }

    private fun initRecycler() {
        val itemDecoration = MiddleGridItemDecoration(requireContext(), R.dimen.size_14dp)
        binding.recycler.layoutManager = GridLayoutManager(requireContext(), 5)
        binding.recycler.addItemDecoration(itemDecoration)
        adapter.onClickListener = this
        binding.recycler.adapter = adapter
    }

    @SingleClick
    override fun onClick(v: View?) {
        if (v!!.id == R.id.item_label_text) {
            val pos = AdapterUtils.getHolder(v).bindingAdapterPosition
            val movieFragment: MovieFragment = requireParentFragment() as MovieFragment
            movieFragment.selectTab(pos)
        }
        dismissAllowingStateLoss()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.recycler.adapter = null
        _binding = null
    }
}