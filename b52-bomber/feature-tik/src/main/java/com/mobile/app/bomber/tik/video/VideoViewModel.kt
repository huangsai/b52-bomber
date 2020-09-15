package com.mobile.app.bomber.tik.video

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.mobile.app.bomber.data.http.entities.ApiFile
import com.mobile.guava.jvm.domain.Source
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.mobile.app.bomber.common.base.MyBaseViewModel
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class VideoViewModel @Inject constructor() : MyBaseViewModel() {

    /**
     * 流程：上传视频文件和封面图片->同步检测是否上传成功
     */
    fun uploadVideo(
            video: File,
            image: File,
            desc: String,
            label: String,
            latitude: Double,
            longitude: Double
    ): LiveData<Source<Pair<ApiFile, ApiFile>>> {
        val result = MutableLiveData<Source<Pair<ApiFile, ApiFile>>>()
        viewModelScope.launch(Dispatchers.IO) {
            val source = uploadRepository.uploadVideo(
                    video, image, desc, label, latitude, longitude
            )
            withContext(Dispatchers.Main) {
                result.value = source
            }
        }
        return result
    }

    override fun onCleared() {
        super.onCleared()
        Timber.e("clean--------------")
    }
}