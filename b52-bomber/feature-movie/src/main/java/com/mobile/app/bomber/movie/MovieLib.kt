package com.mobile.app.bomber.movie

import com.mobile.app.bomber.data.DataLib
import com.mobile.app.bomber.movie.dagger.DaggerMovieComponent
import com.mobile.app.bomber.movie.dagger.MovieComponent
import com.mobile.app.bomber.runner.RunnerLib

object MovieLib {

    val component: MovieComponent by lazy {
        DaggerMovieComponent.factory().create(
                DataLib.component,
                RunnerLib.component
        )
    }
}