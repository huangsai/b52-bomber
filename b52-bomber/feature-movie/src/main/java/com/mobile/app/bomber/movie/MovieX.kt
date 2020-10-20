package com.mobile.app.bomber.movie

import com.mobile.app.bomber.data.DataX
import com.mobile.app.bomber.movie.dagger.DaggerMovieComponent
import com.mobile.app.bomber.movie.dagger.MovieComponent
import com.mobile.app.bomber.runner.RunnerX

object MovieX {

    val component: MovieComponent by lazy {
        DaggerMovieComponent.factory().create(
                DataX.component,
                RunnerX.component
        )
    }
}