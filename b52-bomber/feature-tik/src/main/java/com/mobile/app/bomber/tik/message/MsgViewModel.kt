package com.mobile.app.bomber.tik.message

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.mobile.app.bomber.common.base.MyBaseViewModel
import com.mobile.app.bomber.data.DataX
import com.mobile.app.bomber.data.db.entities.DbTikMessageKey
import com.mobile.app.bomber.data.http.entities.ApiFollow
import com.mobile.app.bomber.data.http.entities.ApiUsermsg
import com.mobile.app.bomber.data.http.entities.ApiVideo
import com.mobile.guava.jvm.domain.Source
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MsgViewModel @Inject constructor() : MyBaseViewModel() {

    fun postUserMsg(msgtype: Int, timestemp: Int): LiveData<Source<List<ApiUsermsg.Item>>> {
        return flow { emit(msgRepository.postUserMsg(msgtype, timestemp)) }
                .asLiveData(Dispatchers.IO)
    }

    fun follow(targetUserId: Long, notFollowing: Int): LiveData<Source<ApiFollow>> {
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


//    fun getKeys(): LiveData<List<DbTikMessageKey>> {
//        return msgRepository.getKeys()
//                .asLiveData(Dispatchers.IO)
//    }

    fun getKey(): LiveData<DbTikMessageKey?> {
        return liveData(Dispatchers.IO) {
            emit(msgRepository.getKey())
        }
    }

    fun addKey(data: String): LiveData<DbTikMessageKey> {
        return liveData(Dispatchers.IO) {
            emit(msgRepository.addKey(data))
        }
    }

    fun clearKeys(): LiveData<Int> {
        return liveData(Dispatchers.IO) {
            emit(msgRepository.clearKeys())
        }
    }

    fun deleteKey(obj: DbTikMessageKey): LiveData<Int> {
        return liveData(Dispatchers.IO) {
            emit(msgRepository.deleteKey(obj))
        }
    }
}