package com.mobile.app.bomber.data.repository

import com.mobile.app.bomber.data.db.AppDatabase
import com.mobile.app.bomber.data.files.AppPrefsManager
import com.mobile.app.bomber.data.http.entities.*
import com.mobile.app.bomber.data.http.service.DataService
import com.mobile.guava.data.toSource
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
    private var atd: Long = 0

    suspend fun videosOfHot(pager: Pager) = queryVideos(pager, "blank", "playcount")

    suspend fun videosOfNew(pager: Pager) = queryVideos(pager, "blank", "new")

    suspend fun videosOfNewPlayCount(pager: Pager) = queryVideos(pager, "blank", "playcount")

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

    suspend fun fixedAd(): Source<ApiFixedad> {
        val call = dataService.getFixedad()
        return try {
            call.execute().toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun videosOfUser(pager: Pager, _userId: Long): Source<List<ApiVideo.Video>> {
        if (pager.isReachedTheEnd) return Source.Success(emptyList())
        val call = dataService.videosOfUser(_userId, pager.requestPage, pager.pageSize, userId, orBlankToken)
        return callApiVideo(call, pager)
    }

    suspend fun videosOfUserCount(pager: Pager, _userId: Long): Source<ApiVideo> {
        val call = dataService.videosOfUser(_userId, pager.requestPage, pager.pageSize, userId, orBlankToken)
        return try {
            call.execute().toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun videosOfLikeCount(pager: Pager, _userId: Long): Source<ApiVideo> {
        val call = dataService.videosOfLike(_userId, pager.requestPage, pager.pageSize)
        return try {
            call.execute().toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
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
                userId, orBlankToken, pager.requestPage, pager.pageSize
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
            call.execute().toSource {
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
        val type = if (video.adId == atd) {
            0
        } else {
            1
        }
        val req = ApiLike.Req(userId, token, if (type == 0) video.videoId else video.adId!!, if (video.isLiking) -1 else 1, type.toLong())
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

    suspend fun playDuration(videoId: Long, aid: Long?, duration: Long,type: Long): Source<Nope> {
        val req = ApiDurationReq(
                userId, appPrefsManager.getDeviceId(), 1, videoId, aid, duration / 1000L,type
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
        var Tourist:String  = ""
        var TouristUid:Long  = 0
        if (!appPrefsManager.isLogin()) {
            TouristUid = 0
            Tourist = "blank"
        } else {
            TouristUid = userId
            Tourist = token
        }
        val call = dataService.videoById(TouristUid, Tourist, videoId)
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