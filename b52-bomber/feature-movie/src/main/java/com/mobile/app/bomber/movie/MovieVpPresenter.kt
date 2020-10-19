package com.mobile.app.bomber.movie

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.mobile.app.bomber.movie.common.CommonListFragment
import com.mobile.app.bomber.movie.databinding.MovieFragmentMovieBinding
import com.mobile.app.bomber.movie.databinding.MovieItemVpBinding
import com.mobile.app.bomber.movie.top.TopListFragment
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem

class MovieVpPresenter(
        private var fragment: MovieFragment,
        private var labels: ArrayList<String>,
        private var movieBinding: MovieFragmentMovieBinding
) : SimpleRecyclerItem() {

    override fun bind(holder: AdapterViewHolder) {
        val binding = holder.binding(MovieItemVpBinding::bind)
        binding.viewPager.adapter = MyAdapter(fragment.requireActivity(), labels)
        binding.viewPager.offscreenPageLimit = labels.size
        val tabLayoutMediator = TabLayoutMediator(movieBinding.layoutTab, binding.viewPager, TabLayoutMediator.TabConfigurationStrategy { tab, position -> tab.text = labels[position] })
        tabLayoutMediator.attach()
    }

    override fun getLayout(): Int {
        return R.layout.movie_item_vp
    }

    private class MyAdapter(
            activity: FragmentActivity,
            private val labels: List<String>
    ) : FragmentStateAdapter(activity) {

        override fun createFragment(position: Int): Fragment {
            return if (position == 0) {
                TopListFragment.newInstance(position)
            } else {
                CommonListFragment.newInstance(position, labels[position])
            }
        }

        override fun getItemCount(): Int {
            return labels.size
        }
    }
}