package com.mobile.app.bomber.tik.mine

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.mobile.app.bomber.data.http.entities.*
import com.mobile.guava.jvm.domain.Source
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import com.mobile.app.bomber.common.base.MyBaseViewModel
import java.io.File
import javax.inject.Inject

class MeViewModel @Inject constructor() : MyBaseViewModel() {

    fun getUserInfo(uid: Long,selfUid: Long): LiveData<Source<ApiUser>> {
        return flow { emit(userRepository.getUserInfo(uid,true,selfUid)) }.asLiveData(Dispatchers.IO)
    }

    fun updateBirthday(birthday: String): LiveData<Source<Nope>> {
        return flow { emit(userRepository.updateBirthday(birthday)) }.asLiveData(Dispatchers.IO)
    }

    fun updateGender(gender: Int): LiveData<Source<Nope>> {
        return flow { emit(userRepository.updateGender(gender)) }.asLiveData(Dispatchers.IO)
    }

    fun updateSign(sign: String): LiveData<Source<Nope>> {
        return flow { emit(userRepository.updateSign(sign)) }
                .asLiveData(Dispatchers.IO)
    }

    fun updateNickname(nickName: String): LiveData<Source<Nope>> {
        return flow { emit(userRepository.updateNickname(nickName)) }
                .asLiveData(Dispatchers.IO)
    }

    fun updateWechatID(wechatID: String): LiveData<Source<Nope>> {
        return flow { emit(userRepository.updateWechatID(wechatID)) }
                .asLiveData(Dispatchers.IO)
    }

    fun follow(targetUserId: Long, isToFollow: Int): LiveData<Source<ApiFollow>> {
        return flow { emit(userRepository.follow(targetUserId, isToFollow)) }
                .asLiveData(Dispatchers.IO)
    }

    fun isFollowing(targetUserId: Long): LiveData<Source<ApiIsFollowing>> {
        return flow { emit(userRepository.isFollowing(targetUserId)) }
                .asLiveData(Dispatchers.IO)
    }

    fun updateHeadPic(imgFile: File): LiveData<Source<ApiFile>> {
        return flow { emit(uploadRepository.uploadProfile(imgFile)) }
                .asLiveData(Dispatchers.IO)
    }

    fun followList(uid: Long): LiveData<Source<List<ApiFollow.Follow>>> {
        return flow { emit(userRepository.followList(uid)) }
                .asLiveData(Dispatchers.IO)
    }

    fun fanList(uid: Long): LiveData<Source<List<ApiFollow.Follow>>> {
        return flow { emit(userRepository.fanList(uid)) }
                .asLiveData(Dispatchers.IO)
    }

    fun getUserCount(uid: Long): LiveData<Source<ApiUserCount>> {
        return flow { emit(userRepository.getUserCount(uid)) }
                .asLiveData(Dispatchers.IO)
    }

    fun videosOfUser(pager: Pager, uid: Long): LiveData<Source<List<ApiVideo.Video>>> {
        return flow { emit(videoRepository.videosOfUser(pager, uid)) }
                .asLiveData(Dispatchers.IO)
    }

    fun videosOfUserCount(pager: Pager, uid: Long): LiveData<Source<ApiVideo>> {
        return flow { emit(videoRepository.videosOfUserCount(pager, uid)) }
                .asLiveData(Dispatchers.IO)
    }

    fun videosOfLikeCount(pager: Pager, uid: Long): LiveData<Source<ApiVideo>> {
        return flow { emit(videoRepository.videosOfLikeCount(pager, uid)) }
                .asLiveData(Dispatchers.IO)
    }

    fun videosOfLike(pager: Pager, uid: Long): LiveData<Source<List<ApiVideo.Video>>> {
        return flow { emit(videoRepository.videosOfLike(pager, uid)) }
                .asLiveData(Dispatchers.IO)
    }
}