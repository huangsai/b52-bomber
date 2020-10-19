package com.mobile.app.bomber.movie.top

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.mobile.app.bomber.movie.MovieFragment
import com.mobile.app.bomber.movie.R
import com.mobile.app.bomber.movie.common.CommonListFragment
import com.mobile.app.bomber.movie.databinding.MovieFragmentMovieBinding
import com.mobile.app.bomber.movie.databinding.MovieItemMovieContentBinding
import com.pacific.adapter.AdapterViewHolder
import com.pacific.adapter.SimpleRecyclerItem

class MovieContentPresenter(
        private var fragment: MovieFragment,
        private var labels: ArrayList<String>,
        private var movieBinding: MovieFragmentMovieBinding
) : SimpleRecyclerItem() {

    override fun bind(holder: AdapterViewHolder) {
        val binding = holder.binding(MovieItemMovieContentBinding::bind)
        binding.movieItemMovieContent.adapter = MyAdapter(fragment.requireActivity(), labels)
        binding.movieItemMovieContent.offscreenPageLimit = labels.size
        val tabLayoutMediator = TabLayoutMediator(
                movieBinding.layoutTab,
                binding.movieItemMovieContent
        ) { tab, position ->
            tab.text = labels[position]
        }
        tabLayoutMediator.attach()
    }

    override fun getLayout(): Int {
        return R.layout.movie_item_movie_content
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