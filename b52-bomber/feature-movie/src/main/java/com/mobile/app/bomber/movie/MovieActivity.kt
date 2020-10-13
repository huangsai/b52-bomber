package com.mobile.app.bomber.movie

import android.os.Bundle
import com.mobile.app.bomber.common.base.MyBaseActivity
import com.mobile.app.bomber.movie.databinding.MovieActivityMovieBinding

class MovieActivity : MyBaseActivity() {

    private lateinit var binding: MovieActivityMovieBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MovieActivityMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
                .disallowAddToBackStack()
                .add(
                        R.id.fragment_container_view,
                        MovieFragment.newInstance(0),
                        MovieFragment::class.java.simpleName
                )
                .commit()
    }
}