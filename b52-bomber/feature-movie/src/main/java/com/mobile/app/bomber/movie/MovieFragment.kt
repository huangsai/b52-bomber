package com.mobile.app.bomber.movie

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.tabs.TabLayout
import com.mobile.app.bomber.common.base.Msg
import com.mobile.app.bomber.common.base.MyBaseFragment
import com.mobile.app.bomber.common.base.tool.SingleClick
import com.mobile.app.bomber.movie.common.CommonListFragment
import com.mobile.app.bomber.movie.databinding.MovieFragmentMovieBinding
import com.mobile.app.bomber.movie.top.TopListFragment
import com.mobile.app.bomber.runner.features.ApiMovieFragment
import com.mobile.guava.android.mvvm.showDialogFragment
import com.mobile.guava.jvm.domain.Source
import com.mobile.guava.jvm.extension.exhaustive
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class MovieFragment : MyBaseFragment(), ApiMovieFragment, View.OnClickListener, TabLayout.OnTabSelectedListener {

    private val model: MovieViewModel by viewModels { MovieX.component.viewModelFactory() }

    private var _binding: MovieFragmentMovieBinding? = null
    private val binding: MovieFragmentMovieBinding get() = _binding!!
    private val tabLabels = ArrayList<String>()
    private var textv: TextView? = null;
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        _binding = MovieFragmentMovieBinding.inflate(inflater, container, false)
        binding.menu.setOnClickListener(this)

        binding.layoutTab.addOnTabSelectedListener(this)
        binding.layoutTab.getTabAt(0)
        return binding.root


    }

    override fun onResume() {
        super.onResume()
        if (onResumeCount == 1) {
            loadTabLabels()
        }
    }

    private fun loadTabLabels() {
        lifecycleScope.launch(Dispatchers.IO) {
            val source = model.getLabel()
            source.dataOrNull()?.let {
                tabLabels.add("精彩")
                tabLabels.addAll(it)
            }
            withContext(Dispatchers.Main) {
                when (source) {
                    is Source.Success -> {
                        binding.viewPager.adapter = MyAdapter(childFragmentManager, tabLabels)
//                        val tabLayoutMediator = TabLayoutMediator(binding.layoutTab, binding.viewPager) { tab, position ->
//                            tab.text = tabLabels[position]
//
//                        }
//                        tabLayoutMediator.attach()
                        binding.viewPager.setOffscreenPageLimit(tabLabels.size)
                        binding.layoutTab.setupWithViewPager(binding.viewPager)
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

    @SingleClick
    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.menu -> {
                showDialogFragment(MovieLabelDialogFragment.newInstance(tabLabels))
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.viewPager.adapter = null
        _binding = null
    }

    companion object {

        @JvmStatic
        fun newInstance(position: Int): MovieFragment = MovieFragment().apply {
            arguments = Bundle().apply {
                putInt("position", position)
            }
        }
    }

    private class MyAdapter(
            fm: FragmentManager,
            private val labels: List<String>
    ) : FragmentPagerAdapter(fm) {

        override fun getItem(position: Int): Fragment {
            return if (position == 0) {
                TopListFragment.newInstance(position)
            } else {
                CommonListFragment.newInstance(position, labels[position])
            }
        }


        override fun getCount(): Int {
            return labels.size
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return labels.get(position)
        }
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {

    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        tab?.setCustomView(null)
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        val textView = TextView(activity)
        val selectedSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, 16f, resources.displayMetrics)
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, selectedSize)
        textView.text = tab?.text

        textView.setTextColor(getResources().getColor(R.color.color_text_ffcc00));
        tab?.customView = textView
    }
}