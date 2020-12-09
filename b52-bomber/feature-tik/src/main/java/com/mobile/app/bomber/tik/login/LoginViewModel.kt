package com.mobile.app.bomber.tik.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.mobile.app.bomber.common.base.MyBaseViewModel
import com.mobile.app.bomber.data.http.entities.*
import com.mobile.guava.android.mvvm.AndroidX
import com.mobile.guava.jvm.coroutines.Bus
import com.mobile.guava.jvm.domain.Source
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class LoginViewModel @Inject constructor() : MyBaseViewModel() {

    fun logout() {
        userRepository.logout()
        Bus.offer(AndroidX.BUS_LOGOUT)
    }

    fun login(telephone: String, verifyCode: String): LiveData<Source<ApiToken>> {
        return flow { emit(userRepository.login(telephone, verifyCode)) }
                .asLiveData(Dispatchers.IO)
    }

    fun fastLogin(): LiveData<Source<ApiToken>> {
        return flow { emit(userRepository.fastLogin()) }
                .asLiveData(Dispatchers.IO)
    }

    fun getVerifyCode(telephone: String): LiveData<Source<Nope>> {
        return flow { emit(userRepository.getVerifyCode(telephone)) }
                .asLiveData(Dispatchers.IO)
    }

    fun adMsg(type: Int): LiveData<Source<ApiAdMsg>> {
        return flow { emit(adRepository.adMsg(type)) }
                .asLiveData(Dispatchers.IO)
    }

    fun ckVersino(): LiveData<Source<ApiVersion>> {
        return flow { emit(versionRepository.checkVersion()) }
                .asLiveData(Dispatchers.IO)
    }

    fun ad(type: Int): LiveData<Source<ApiAd>> {
        return flow { emit(adRepository.ad(type)) }
                .asLiveData(Dispatchers.IO)
    }

    fun shareAppUrl(): LiveData<Source<ApiShareUrl>> {
        return flow { emit(shareRepository.getShareUrl()) }
                .asLiveData(Dispatchers.IO)
    }

    fun downLoadUrl(): LiveData<Source<ApiDownLoadUrl>> {
        return flow { emit(shareRepository.getDownLoadUrl()) }
                .asLiveData(Dispatchers.IO)
    }

}