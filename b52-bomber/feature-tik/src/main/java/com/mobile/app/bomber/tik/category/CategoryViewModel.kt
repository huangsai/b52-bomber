package com.mobile.app.bomber.tik.category

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.mobile.guava.jvm.domain.Source
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import com.mobile.app.bomber.common.base.MyBaseViewModel
import com.mobile.app.bomber.data.http.entities.*
import javax.inject.Inject

class CategoryViewModel @Inject constructor() : MyBaseViewModel() {

    fun videosOfHot(pager: Pager): LiveData<Source<List<ApiVideo.Video>>> {
        return flow { emit(videoRepository.videosOfHot(pager)) }
                .asLiveData(Dispatchers.IO)
    }

    fun videoOfNew(pager: Pager): LiveData<Source<List<ApiVideo.Video>>> {
        return flow { emit(videoRepository.videosOfNew(pager)) }
                .asLiveData(Dispatchers.IO)
    }

    fun videosOfLabel(pager: Pager, label: String): LiveData<Source<List<ApiVideo.Video>>> {
        return flow { emit(videoRepository.videosOfLabel(pager, label)) }
                .asLiveData(Dispatchers.IO)
    }

    fun ranksOfPlay(time: Long, pager: Pager): LiveData<Source<List<ApiRank.Rank>>> {
        return flow { emit(userRepository.ranksOfUser("playcount", time, pager)) }
                .asLiveData(Dispatchers.IO)
    }

    fun ranksOfLike(time: Long, pager: Pager): LiveData<Source<List<ApiRank.Rank>>> {
        return flow { emit(userRepository.ranksOfUser("like", time, pager)) }
                .asLiveData(Dispatchers.IO)
    }

    fun follow(targetUserId: Long, notFollowing: Boolean): LiveData<Source<ApiFollow>> {
        return flow { emit(userRepository.follow(targetUserId, notFollowing)) }
                .asLiveData(Dispatchers.IO)
    }

    fun fixedAd(): LiveData<Source<ApiFixedad>> {
        return flow { emit(videoRepository.fixedAd()) }
                .asLiveData(Dispatchers.IO)
    }
}