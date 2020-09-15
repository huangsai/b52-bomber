package com.mobile.app.bomber.movie.dagger

import androidx.lifecycle.ViewModel
import com.mobile.app.bomber.movie.MovieViewModel
import com.mobile.app.bomber.movie.player.PlayerViewModel
import com.mobile.app.bomber.movie.search.SearchViewModel
import com.mobile.guava.android.mvvm.dagger.FeatureScopeViewModelFactoryBinder
import com.mobile.guava.android.mvvm.dagger.ViewModelKey
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module(includes = [FeatureScopeViewModelFactoryBinder::class])
abstract class MovieBinder {

    @Binds
    @IntoMap
    @ViewModelKey(MovieViewModel::class)
    abstract fun bindMovieViewModel(it: MovieViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(PlayerViewModel::class)
    abstract fun bindPlayerViewModel(it: PlayerViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SearchViewModel::class)
    abstract fun bindSearchViewModel(it: SearchViewModel): ViewModel
}