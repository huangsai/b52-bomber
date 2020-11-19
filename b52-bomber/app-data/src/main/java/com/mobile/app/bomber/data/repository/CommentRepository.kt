package com.mobile.app.bomber.data.repository

import com.mobile.app.bomber.data.db.AppDatabase
import com.mobile.app.bomber.data.files.AppPrefsManager
import com.mobile.app.bomber.data.http.entities.ApiComment
import com.mobile.app.bomber.data.http.entities.ApiCreateComment
import com.mobile.app.bomber.data.http.entities.ApiLike
import com.mobile.app.bomber.data.http.entities.Nope
import com.mobile.guava.data.toSource
import com.mobile.app.bomber.data.http.service.DataService
import com.mobile.guava.jvm.domain.Source
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CommentRepository @Inject constructor(
        dataService: DataService,
        db: AppDatabase,
        appPrefsManager: AppPrefsManager
) : BaseRepository(dataService, db, appPrefsManager) {

    suspend fun comments(videoId: Long): Source<List<ApiComment.Comment>> {
        val token = appPrefsManager.getToken().ifEmpty { "blank" }
        val call = dataService.comments(appPrefsManager.getUserId(), token, videoId)
        return try {
            call.toSource { it.comments.orEmpty() }
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun createComment(
            videoId: Long,
            content: String,
            toCommendId: Long,
            toUserId: Long,
            at: String,
            type: Long
    ): Source<ApiCreateComment> {
        val req = ApiCreateComment.Req(
                appPrefsManager.getUserId(),
                appPrefsManager.getToken(),
                videoId,
                toCommendId,
                toUserId,
                content,
                at,
                type
        )
        return try {
            dataService.createComment(req).toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun likeComment(comment: ApiComment.Comment): Source<Nope> {
        val req = ApiLike.ReqComment(
                appPrefsManager.getUserId(),
                appPrefsManager.getToken(),
                comment.id,
                if (comment.isLiking) 1 else -1
        )
        return try {
            dataService.likeComment(req).toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun deleteComment(commentId: Long, videoId: Long): Source<Nope> {
        val req = ApiComment.ReqDelete(
                appPrefsManager.getUserId(),
                appPrefsManager.getToken(),
                commentId,
                videoId
        )
        return try {
            dataService.deleteComment(req).toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
    }
}