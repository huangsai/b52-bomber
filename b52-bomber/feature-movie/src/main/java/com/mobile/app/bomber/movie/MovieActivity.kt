package com.mobile.app.bomber.movie

import android.os.Bundle
import androidx.databinding.DataBindingUtil.setContentView
import com.mobile.app.bomber.common.base.MyBaseActivity
import com.mobile.app.bomber.movie.databinding.MovieActivityMovieBinding

import com.mobile.app.bomber.movie.player.PlayerActivity
import com.mobile.guava.android.mvvm.newStartActivity
import org.chromium.support_lib_boundary.util.Features


class MovieActivity : MyBaseActivity() {

    private lateinit var binding: MovieActivityMovieBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MovieActivityMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        addFragment(
                R.id.fragment_container_view,
                MovieFragment.newInstance(0)
        )


//        PlayerActivity.start(this, ApiMovie.Movie(0))

    }
}