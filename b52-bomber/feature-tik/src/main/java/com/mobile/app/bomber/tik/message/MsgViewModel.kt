package com.mobile.app.bomber.tik.message

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.mobile.app.bomber.common.base.MyBaseViewModel
import com.mobile.app.bomber.data.DataX
import com.mobile.app.bomber.data.http.entities.*
import com.mobile.guava.android.ensureWorkThread
import com.mobile.guava.jvm.domain.Source
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MsgViewModel @Inject constructor() : MyBaseViewModel() {

    fun likeList(pager: Pager): LiveData<Source<List<ApiLikeList.Item>>> {
        return flow { emit(msgRepository.likeList(pager)) }
                .asLiveData(Dispatchers.IO)
    }

    fun atList(pager: Pager): LiveData<Source<List<ApiAtList.Item>>> {
        return flow { emit(msgRepository.atList(pager)) }
                .asLiveData(Dispatchers.IO)
    }

    fun commentList(pager: Pager): LiveData<Source<List<ApiCommentList.Item>>> {
        return flow { emit(msgRepository.commentList(pager)) }
                .asLiveData(Dispatchers.IO)
    }

    fun postUserMsg(msgtype: Int, timestemp: Int): LiveData<Source<List<ApiUsermsg.Item>>> {
        return flow { emit(msgRepository.postUserMsg(msgtype, timestemp)) }
                .asLiveData(Dispatchers.IO)
    }

    fun fanList(): LiveData<Source<List<ApiFollow.Follow>>> {
        return flow { emit(userRepository.fanList()) }
                .asLiveData(Dispatchers.IO)
    }

    fun follow(targetUserId: Long, notFollowing: Boolean): LiveData<Source<ApiFollow>> {
        return flow { emit(userRepository.follow(targetUserId, notFollowing)) }
                .asLiveData(Dispatchers.IO)
    }

    fun videoById(videoId: Long): LiveData<Source<ApiVideo.Video>> {
        return flow { emit(videoRepository.videoById(videoId)) }
                .asLiveData(Dispatchers.IO)
    }

    fun parseVideo(videoJson: String): ApiVideo.Video {
        return DataX.component.json()
                .adapter(ApiVideo.Video::class.java)
                .lenient()
                .fromJson(videoJson)!!
    }
}