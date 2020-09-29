package com.mobile.app.bomber.movie

import androidx.annotation.WorkerThread
import com.mobile.app.bomber.common.base.MyBaseViewModel
import com.mobile.app.bomber.data.http.entities.ApiMovieBanner
import com.mobile.app.bomber.data.http.entities.Nope
import com.mobile.guava.android.ensureWorkThread
import com.mobile.guava.jvm.domain.Source
import javax.inject.Inject

class MovieViewModel @Inject constructor() : MyBaseViewModel() {

    @WorkerThread
    suspend fun getBanner(): Source<List<ApiMovieBanner.Banner>> {
        ensureWorkThread()
        return movieRepository.getBanner()
    }


}