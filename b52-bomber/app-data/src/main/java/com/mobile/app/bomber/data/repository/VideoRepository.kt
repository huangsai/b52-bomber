package com.mobile.app.bomber.data.repository

import com.mobile.app.bomber.data.db.AppDatabase
import com.mobile.app.bomber.data.files.AppPrefsManager
import com.mobile.app.bomber.data.http.entities.*
import com.mobile.guava.data.toSource
import com.mobile.app.bomber.data.http.service.DataService
import com.mobile.guava.jvm.domain.Source
import retrofit2.Call
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideoRepository @Inject constructor(
        dataService: DataService,
        db: AppDatabase,
        appPrefsManager: AppPrefsManager
) : BaseRepository(dataService, db, appPrefsManager) {

    suspend fun videosOfHot(pager: Pager) = queryVideos(pager, "blank", "hot")

    suspend fun videosOfNew(pager: Pager) = queryVideos(pager, "blank", "new")

    suspend fun videosOfCommend(pager: Pager) = queryCommendVideos(pager)

    suspend fun videosOfLabel(pager: Pager, label: String) = queryVideos(pager, label, "blank")

    suspend fun videosOfFollow(pager: Pager): Source<List<ApiVideo.Video>> {
        if (!appPrefsManager.isLogin()) {
            return Source.Error(sourceException403)
        }
        if (pager.isReachedTheEnd) return Source.Success(emptyList())
        val call = dataService.videosOfFollow(userId, token, pager.requestPage, pager.pageSize)
        return callApiVideo(call, pager)
    }

    suspend fun videosOfNearby(pager: Pager, latitude: Double, longitude: Double): Source<List<ApiVideo.Video>> {
        if (pager.isReachedTheEnd) return Source.Success(emptyList())
        val call = dataService.videosOfNearby(
                userId, orBlankToken, latitude, longitude, pager.requestPage, pager.pageSize
        )
        return callApiVideo(call, pager)
    }
    suspend fun fixedAd(platform: Int): Source<ApiFixedad> {
        val call = dataService.getFixedad(platform)
        return try {
            call.execute().toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun videosOfUser(pager: Pager, _userId: Long): Source<List<ApiVideo.Video>> {
        if (pager.isReachedTheEnd) return Source.Success(emptyList())
        val call = dataService.videosOfUser(_userId, pager.requestPage, pager.pageSize)
        return callApiVideo(call, pager)
    }

    suspend fun videosOfLike(pager: Pager, _userId: Long): Source<List<ApiVideo.Video>> {
        if (pager.isReachedTheEnd) return Source.Success(emptyList())
        val call = dataService.videosOfLike(_userId, pager.requestPage, pager.pageSize)
        return callApiVideo(call, pager)
    }

    private suspend fun queryCommendVideos(
            pager: Pager
    ): Source<List<ApiVideo.Video>> {
        val call = dataService.queryCommendVideos(
                userId, orBlankToken,pager.requestPage,pager.pageSize
        )
        return callCommendApiVideo(call)
    }
    private suspend fun queryVideos(
            pager: Pager, label: String, sort: String
    ): Source<List<ApiVideo.Video>> {
        if (pager.isReachedTheEnd) return Source.Success(emptyList())
        val call = dataService.queryVideos(
                userId, orBlankToken, label, sort, pager.requestPage, pager.pageSize
        )
        return callApiVideo(call, pager)
    }

    private suspend fun callCommendApiVideo(
            call: Call<ApiVideo>
    ): Source<List<ApiVideo.Video>> {
         return try {
             call.execute().toSource{
                 it.videos.orEmpty()
             }
         } catch (e: Exception) {
             errorSource(e)
         }
    }

    private suspend fun callApiVideo(
            call: Call<ApiVideo>, pager: Pager
    ): Source<List<ApiVideo.Video>> {
        pager.isBusy = true
        return try {
            call.execute().toSource {
                pager.nextPage(it.totalPage)
                it.videos.orEmpty()
            }
        } catch (e: Exception) {
            pager.isBusy = false
            errorSource(e)
        }
    }

    suspend fun likeVideo(video: ApiVideo.Video): Source<ApiLike> {
        val req = ApiLike.Req(userId, token, video.videoId, if (video.isLiking) -1 else 1)
        return try {
            dataService.likeVideo(req).toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun playVideo(videoId: Long): Source<Nope> {
        val call = ApiVideo.ReqPlay(
                videoId, userId, 1, appPrefsManager.getDeviceId()
        )
        return try {
            dataService.playVideo(call).execute().toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun playDuration(videoId: Long, aid: Long?, duration: Long): Source<Nope> {
        val req = ApiDurationReq(
                userId, appPrefsManager.getDeviceId(), 1, videoId, aid, duration / 1000L
        )
        return try {
            dataService.playDuration(req).execute().toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun shareVideo(videoId: Long): Source<Nope> {
        return try {
            dataService.shareVideo(ApiVideo.ReqShare(videoId)).toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun videoById(videoId: Long): Source<ApiVideo.Video> {
        val call = dataService.videoById(userId, token, videoId)
        return try {
            call.execute().toSource {
                it.video
            }
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun getDownloadUrl(): Source<ApiDownLoadUrl> {
        return try {
            dataService.getDownLoadUrl().toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
    }

}