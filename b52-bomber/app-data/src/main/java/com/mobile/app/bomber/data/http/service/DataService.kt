package com.mobile.app.bomber.data.http.service

import com.mobile.app.bomber.data.http.entities.*
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DataService {

    @POST("${API_USER}/login/")
    fun login(@Body body: ApiToken.AReq): Call<ApiToken>

    @POST("${API_USER}/fastlogin/")
    fun fastLogin(@Body body: ApiToken.Req): Call<ApiToken>

    @GET("${API_USER}/getverifycode/{phonenum}/")
    fun verifyCode(@Path("phonenum") telephone: String): Call<Nope>

    @POST("${API_VIDEO}/videolike/")
    fun likeVideo(@Body body: ApiLike.Req): Call<ApiLike>

    @GET("${API_VIDEO}/getdistancevideolist/{uid}/{fastkey}/{lat}/{lng}/{page}/{size}/ ")
    fun videosOfNearby(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String,
            @Path("lat") lat: Double,
            @Path("lng") lng: Double,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiVideo>

    @GET("${API_VIDEO}/getfollowvideolist/{uid}/{fastkey}/{page}/{size}/")
    fun videosOfFollow(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiVideo>

    @GET("${API_VIDEO}/getvideolist/{uid}/{fastkey}/{label}/{sort}/{page}/{size}/")
    fun queryVideos(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String,
            @Path("label") label: String,
            @Path("sort") sort: String,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiVideo>

    @GET("${API_USER}/getuserinfo/{uid}/")
    fun user(@Path("uid") uid: Long): Call<ApiUser>

    @POST("${API_USER}/modifyuserInfo/")
    fun updateBirthday(@Body body: ApiUser.BirthdayReq): Call<Nope>

    @POST("${API_USER}/modifyuserInfo/")
    fun updateGender(@Body body: ApiUser.GenderReq): Call<Nope>

    @POST("${API_USER}/modifyuserInfo/")
    fun updateSign(@Body body: ApiUser.SignReq): Call<Nope>

    @POST("${API_USER}/modifyuserInfo/")
    fun updateNickname(@Body body: ApiUser.NicknameReq): Call<Nope>

    @POST("${API_USER}/modifyuserInfo/")
    fun updateWechat(@Body body: ApiUser.WechatReq): Call<Nope>

    @POST("${API_USER}/followone/")
    fun follow(@Body body: ApiFollow.Req): Call<ApiFollow>

    @GET("${API_USER}/isfollowed/{uid}/{fastkey}/{userid}/")
    fun isFollowing(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String,
            @Path("userid") targetUid: Long
    ): Call<ApiIsFollowing>

    @POST("/group1/upload")
    fun uploadFile(@Body body: RequestBody): Call<ApiFile>

    @POST("${API_USER}/changeheadpic/")
    fun syncPicture(@Body body: ApiFile.ReqPicture): Call<Nope>

    @POST("${API_VIDEO}/videoupoload/")
    fun syncVideo(@Body body: ApiFile.ReqVideo): Call<Nope>

    @POST("${API_VIDEO}/videoplay/")
    fun playVideo(@Body body: ApiVideo.ReqPlay): Call<Nope>

    @POST("${API_VIDEO}/sharevideo/")
    fun shareVideo(@Body body: ApiVideo.ReqShare): Call<Nope>

    @GET("${API_VIDEO}/getvideocommentslist/{uid}/{fastkey}/{videoid}/")
    fun comments(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String,
            @Path("videoid") videoid: Long
    ): Call<ApiComment>

    @POST("${API_VIDEO}/videocomments/")
    fun createComment(@Body body: ApiCreateComment.Req): Call<ApiCreateComment>

    @POST("${API_VIDEO}/commentslike/")
    fun likeComment(@Body body: ApiLike.ReqComment): Call<Nope>

    @GET("${API_USER}/getfollowlist/{uid}/{fastkey}/")
    fun followList(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String
    ): Call<ApiFollow>

    @GET("${API_USER}/getfanslist/{uid}/{fastkey}/")
    fun fanList(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String
    ): Call<ApiFans>

    @GET("${API_USER}/getuserrank/{uid}/{fastkey}/{type}/{timestemp}/{page}/{size}/")
    fun ranksOfUser(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String,
            @Path("type") type: String,
            @Path("timestemp") time: Long,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiRank>

    @GET("${API_USER}/getusercount/{uid}/")
    fun getUserCount(
            @Path("uid") uid: Long
    ): Call<ApiUserCount>

    @GET("${API_VIDEO}/getuservideolist/{uid}/{page}/{size}/")
    fun videosOfUser(
            @Path("uid") uid: Long,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiVideo>

    @GET("${API_VIDEO}/getuserlikevideolist/{uid}/{page}/{size}/")
    fun videosOfLike(
            @Path("uid") uid: Long,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiVideo>

    @GET("${API_SYS}/getindexmessage/{type}/")
    fun adMsg(@Path("type") type: Int): Call<ApiAdMsg>

    @GET("${API_SYS}/getadconfig/{type}/")
    fun ad(@Path("type") type: Int): Call<ApiAd>

    @GET("${API_VIDEO}/commentstouser/{uid}/{fastkey}/{page}/{size}/")
    fun commentList(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiCommentList>

    @GET("${API_VIDEO}/commentsatuser/{uid}/{fastkey}/{page}/{size}/")
    fun atList(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiAtList>

    @GET("${API_VIDEO}/userlikeme/{uid}/{fastkey}/{page}/{size}/")
    fun likeList(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiLikeList>

    @POST("${API_VIDEO}/deletevideocomments/")
    fun deleteComment(@Body body: ApiComment.ReqDelete): Call<Nope>

    @GET("${API_VIDEO}/getvideobyid/{uid}/{fastkey}/{videoid}/")
    fun videoById(
            @Path("uid") uId: Long,
            @Path("fastkey") token: String,
            @Path("videoid") videoId: Long
    ): Call<ApiVideoById>

    @GET("${API_USER}/searchuser/{keyword}/")
    fun searchUsers(@Path("keyword") keyword: String): Call<ApiAtUser>

    @POST("${API_VIDEO}/postvideoplayduration/")
    fun playDuration(@Body body: ApiDurationReq): Call<Nope>

    @POST("${API_VIDEO}/postSearchVideoUser/{uid}/{keyword}/{page}/{size}")
    fun SearchVideo(
            @Path("uid") uId: Long,
            @Path("keyword") keyword: String,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiVideo>

    @GET("${API_VIDEO}/getHotKeyTopN")
    fun getHotKey(): Call<ApitHotKey>

    @POST("${API_VIDEO}/postSearchMovie/{keyword}/{page}/{size}")
    fun searchMovie(
            @Path("keyword") keyword: String,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiMovie.Movie>

    @GET("${API_VIDEO}/getVersion/{platform}")
    fun getVersion(@Path("platform") platform: Int
    ): Call<Version>
}