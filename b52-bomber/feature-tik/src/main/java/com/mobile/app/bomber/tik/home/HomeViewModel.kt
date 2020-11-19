package com.mobile.app.bomber.tik.home

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.mobile.app.bomber.data.http.entities.*
import com.mobile.guava.android.ensureWorkThread
import com.mobile.guava.jvm.domain.Source
import com.mobile.app.bomber.common.base.MyBaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class HomeViewModel @Inject constructor() : MyBaseViewModel() {

    val aboutUsers: List<ApiAtUser.User> get() = userRepository.aboutUsers

    @WorkerThread
    suspend fun nearbyVideo(
            pager: Pager, latitude: Double, longitude: Double
    ): Source<List<ApiVideo.Video>> {
        ensureWorkThread()
        return videoRepository.videosOfNearby(pager, latitude, longitude)
    }

    @WorkerThread
    suspend fun videosOfNew(pager: Pager): Source<List<ApiVideo.Video>> {
        ensureWorkThread()
        return videoRepository.videosOfNew(pager)
    }

    @WorkerThread
    suspend fun videosOfCommend(pager: Pager): Source<List<ApiVideo.Video>> {
        ensureWorkThread()
        return videoRepository.videosOfCommend(pager)
    }

    @WorkerThread
    suspend fun videosOfFollow(pager: Pager): Source<List<ApiVideo.Video>> {
        ensureWorkThread()
        return videoRepository.videosOfFollow(pager)
    }

    @WorkerThread
    suspend fun likeVideo(video: ApiVideo.Video): Source<ApiLike> {
        ensureWorkThread()
        return videoRepository.likeVideo(video)
    }

    @WorkerThread
    suspend fun follow(targetUserId: Long, notFollowing: Int): Source<ApiFollow> {
        ensureWorkThread()
        return userRepository.follow(targetUserId, notFollowing)
    }

    @WorkerThread
    suspend fun likeComment(comment: ApiComment.Comment): Source<Nope> {
        ensureWorkThread()
        return commentRepository.likeComment(comment)
    }

    @WorkerThread
    suspend fun createComment(
            videoId: Long, content: String, toCommendId: Long, toUserId: Long, at: String, type: Long
    ): Source<ApiCreateComment> {
        ensureWorkThread()
        return commentRepository.createComment(videoId, content, toCommendId, toUserId, at,type)
    }

    @WorkerThread
    suspend fun comments(videoId: Long): Source<List<ApiComment.Comment>> {
        ensureWorkThread()
        return commentRepository.comments(videoId)
    }

    @WorkerThread
    suspend fun searchUsers(keyword: String): Source<List<ApiAtUser.User>> {
        ensureWorkThread()
        return userRepository.searchUsers(keyword)
    }

    @WorkerThread
    suspend fun deleteComment(commentId: Long, videoId: Long): Source<Nope> {
        ensureWorkThread()
        return commentRepository.deleteComment(commentId, videoId)
    }

    @WorkerThread
    suspend fun shareVideo(videoId: Long): Source<Nope> {
        ensureWorkThread()
        return videoRepository.shareVideo(videoId)
    }

    @WorkerThread
    suspend fun playVideo(videoId: Long): Source<Nope> {
        ensureWorkThread()
        return videoRepository.playVideo(videoId)
    }

    @WorkerThread
    suspend fun playDuration(videoId: Long, aid: Long?, duration: Long): Source<Nope> {
        ensureWorkThread()
        return videoRepository.playDuration(videoId, aid, duration)
    }

    @WorkerThread
    suspend fun videoById(videoId: Long): Source<ApiVideo.Video> {
        ensureWorkThread()
        return videoRepository.videoById(videoId)
    }

    @WorkerThread
    suspend fun shareAppUrl(): Source<ApiDownLoadUrl> {
        ensureWorkThread()
        return shareRepository.getDownloadUrl()
    }
}