package com.mobile.app.bomber.movie

import androidx.annotation.WorkerThread
import com.mobile.app.bomber.common.base.MyBaseViewModel
import com.mobile.app.bomber.data.http.entities.ApiMovie
import com.mobile.app.bomber.data.http.entities.ApiMovieBanner
import com.mobile.app.bomber.data.http.entities.Pager
import com.mobile.guava.android.ensureWorkThread
import com.mobile.guava.jvm.domain.Source
import javax.inject.Inject

class MovieViewModel @Inject constructor() : MyBaseViewModel() {

    @WorkerThread
    suspend fun getBanner(): Source<List<ApiMovieBanner.Banner>> {
        ensureWorkThread()
        return movieRepository.getBanner()
    }

    @WorkerThread
    suspend fun getLabel(): Source<List<String>> {
        ensureWorkThread()
        return movieRepository.getLabel()
    }

    @WorkerThread
    suspend fun getMovieListByLabel(pager: Pager, label: String): Source<List<ApiMovie.Movie>> {
        ensureWorkThread()
        return movieRepository.getMovieListByLabel(pager, label)
    }

    @WorkerThread
    suspend fun getMovieListRecommend(): Source<List<ApiMovie.Movie>> {
        ensureWorkThread()
        return movieRepository.getMovieListRecommend()
    }

    @WorkerThread
    suspend fun getMovieHistory(): Source<List<ApiMovie.Movie>> {
        ensureWorkThread()
        return movieRepository.getMovieHistory()
    }

    @WorkerThread
    suspend fun getMovieGuessLike(): Source<List<ApiMovie.Movie>> {
        ensureWorkThread()
        return movieRepository.getGuessULikeMovieList()
    }
}