package com.mobile.app.bomber.data.repository

import com.mobile.app.bomber.data.db.AppDatabase
import com.mobile.app.bomber.data.files.AppPrefsManager
import com.mobile.app.bomber.data.http.entities.ApiFile
import com.mobile.guava.data.toSource
import com.mobile.app.bomber.data.http.service.DataService
import com.mobile.guava.data.bodyOrThrowException
import com.mobile.guava.jvm.domain.Source
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UploadRepository @Inject constructor(
        dataService: DataService,
        db: AppDatabase,
        appPrefsManager: AppPrefsManager
) : BaseRepository(dataService, db, appPrefsManager) {

    /**
     * 流程：上传图片->同步检测是否上传成功
     */
    suspend fun uploadProfile(image: File): Source<ApiFile> {
        val body = createRequestBody(image, "headpic", "headpic")
        return try {
            val apiFile = dataService.uploadFile(body).execute().bodyOrThrowException()
            val req = ApiFile.ReqPicture(
                    appPrefsManager.getUserId(), appPrefsManager.getToken(), apiFile.url
            )
            dataService.syncPicture(req).execute().toSource {
                apiFile
            }
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    /**
     * 流程：上传视频文件和封面图片->同步检测是否上传成功
     */
    suspend fun uploadVideo(
            video: File,
            image: File,
            desc: String,
            label: String,
            latitude: Double,
            longitude: Double
    ): Source<Pair<ApiFile,ApiFile>> = supervisorScope {
        val bodyVideo = createRequestBody(video, "video", "video")
        val bodyImage = createRequestBody(image, "cover", "cover")
        return@supervisorScope try {
            val deferredVideo = async {
                dataService.uploadFile(bodyVideo).execute().bodyOrThrowException()
            }
            val deferredImage = async {
                dataService.uploadFile(bodyImage).execute().bodyOrThrowException()
            }
            val apiFileVideo = deferredVideo.await()
            val apiFileImage = deferredImage.await()
            val userId = appPrefsManager.getUserId()
            val req = ApiFile.ReqVideo(
                    userId, userId, appPrefsManager.getToken(), apiFileVideo.url, apiFileImage.url,
                    desc, label, latitude, longitude
            )
            dataService.syncVideo(req).execute().toSource {
                Pair(apiFileVideo, apiFileImage)
            }
        } catch (e: Exception) {
            errorSource<Pair<ApiFile,ApiFile>>(e)
        }
    }

    private fun createRequestBody(file: File, path: String, scene: String): RequestBody {
        require(path == scene)
        return MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                        "file",
                        file.name,
                        file.asRequestBody("multipart/form-data".toMediaType())
                )
                .addFormDataPart("path", path)
                .addFormDataPart("scene", scene)
                .addFormDataPart("output", "json")
                .build()
    }
}