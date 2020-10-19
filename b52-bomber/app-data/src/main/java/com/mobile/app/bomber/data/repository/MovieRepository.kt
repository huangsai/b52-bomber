package com.mobile.app.bomber.data.repository

import com.mobile.app.bomber.data.db.AppDatabase
import com.mobile.app.bomber.data.files.AppPrefsManager
import com.mobile.app.bomber.data.http.entities.*
import com.mobile.app.bomber.data.http.service.DataService
import com.mobile.guava.data.toSource
import com.mobile.guava.jvm.domain.Source
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MovieRepository @Inject constructor(
        dataService: DataService,
        db: AppDatabase,
        appPrefsManager: AppPrefsManager
) : BaseRepository(dataService, db, appPrefsManager) {

    suspend fun getBanner(): Source<List<ApiMovieBanner.Banner>> {
        val call = dataService.getBanner(0)
        return try {
            call.execute().toSource {
                it.banner.orEmpty()
            }
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun getLabel(): Source<List<String>> {
        val call = dataService.getMovieLabel()
        return try {
            call.execute().toSource {
                it.labels.orEmpty()
            }
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun getMovieListByLabel(pager: Pager, label: String): Source<List<ApiMovie.Movie>> {
        if (pager.isReachedTheEnd) return Source.Success(emptyList())
        pager.isBusy = true
        val call = dataService.getMovieListByLabel(
                ApiMovie.ReqLabel(appPrefsManager.getUserId(), pager.requestPage, pager.pageSize,
                        "new", appPrefsManager.getToken(), label))
        return try {
            call.execute().toSource {
                pager.nextPage(it.totalPage)
                it.movies.orEmpty()
            }
        } catch (e: Exception) {
            pager.isBusy = false
            errorSource(e)
        }
    }

    suspend fun getMovieListRecommend(): Source<List<ApiMovie.Movie>> {
        val call = dataService.getMovieListRecommend(appPrefsManager.getUserId())
        return try {
            call.execute().toSource {
                it.movies.orEmpty()
            }
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun getMovieDetail(movieId: Long, uId: Long, deviceId: String): Source<ApiMovieDetail> {
        val call = dataService.getMovieDetail(movieId, uId, deviceId)
        return try {
            call.execute().toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun postMovieLike(movieId: Int, isLike: Int): Source<Nope> {
        val req = ApiMovieId(
                userId, token, movieId, isLike
        )
        return try {
            dataService.postMovieLike(req).execute().toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun postMovieCollection(movieId: Int, isCollection: Int): Source<ApiSubmitCollection> {
        val req = ApiMovieCollection(
                userId, movieId, isCollection, token
        )
        return try {
            dataService.postMovieCollection(req).execute().toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun getMovieHistory(): Source<List<ApiMovie.Movie>> {
        val call = dataService.getMovieHistory(ApiMovieHistory.Req(appPrefsManager.getToken(), appPrefsManager.getUserId()))
        return try {
            call.execute().toSource {
                it.movies.orEmpty()
            }

        } catch (e: Exception) {
            errorSource(e)
        }
    }


    suspend fun getGuessULikeMovieList(): Source<List<ApiMovie.Movie>> {
        val call = dataService.getGuessULikeMovieList(appPrefsManager.getUserId())
        return try {
            call.execute().toSource {
                it.movies.orEmpty()
            }

        } catch (e: Exception) {
            errorSource(e)
        }
    }
}