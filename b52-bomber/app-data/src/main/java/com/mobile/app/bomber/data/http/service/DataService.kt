package com.mobile.app.bomber.data.http.service

import com.mobile.app.bomber.data.http.entities.*
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DataService {

    @POST("/login/")
    fun login(@Body body: ApiToken.AReq): Call<ApiToken>

    @POST("/fastlogin/")
    fun fastLogin(@Body body: ApiToken.Req): Call<ApiToken>

    @GET("/getverifycode/{phonenum}/")
    fun verifyCode(@Path("phonenum") telephone: String): Call<Nope>

    @POST("/videolike/")
    fun likeVideo(@Body body: ApiLike.Req): Call<ApiLike>

    @GET("/getdistancevideolist/{uid}/{fastkey}/{lat}/{lng}/{page}/{size}/ ")
    fun videosOfNearby(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String,
            @Path("lat") lat: Double,
            @Path("lng") lng: Double,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiVideo>

    @GET("/getfollowvideolist/{uid}/{fastkey}/{page}/{size}/")
    fun videosOfFollow(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiVideo>

    @GET("/getvideolist/{uid}/{fastkey}/{label}/{sort}/{page}/{size}/")
    fun queryVideos(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String,
            @Path("label") label: String,
            @Path("sort") sort: String,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiVideo>

    @GET("/getuserinfo/{uid}/")
    fun user(@Path("uid") uid: Long): Call<ApiUser>

    @POST("/modifyuserInfo/")
    fun updateBirthday(@Body body: ApiUser.BirthdayReq): Call<Nope>

    @POST("/modifyuserInfo/")
    fun updateGender(@Body body: ApiUser.GenderReq): Call<Nope>

    @POST("/modifyuserInfo/")
    fun updateSign(@Body body: ApiUser.SignReq): Call<Nope>

    @POST("/modifyuserInfo/")
    fun updateNickname(@Body body: ApiUser.NicknameReq): Call<Nope>

    @POST("/modifyuserInfo/")
    fun updateWechat(@Body body: ApiUser.WechatReq): Call<Nope>

    @POST("/followone/")
    fun follow(@Body body: ApiFollow.Req): Call<ApiFollow>

    @GET("/isfollowed/{uid}/{fastkey}/{userid}/")
    fun isFollowing(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String,
            @Path("userid") targetUid: Long
    ): Call<ApiIsFollowing>

    @POST("/group1/upload")
    fun uploadFile(@Body body: RequestBody): Call<ApiFile>

    @POST("/changeheadpic/")
    fun syncPicture(@Body body: ApiFile.ReqPicture): Call<Nope>

    @POST("/videoupoload/")
    fun syncVideo(@Body body: ApiFile.ReqVideo): Call<Nope>

    @POST("/videoplay/")
    fun playVideo(@Body body: ApiVideo.ReqPlay): Call<Nope>

    @POST("/sharevideo/")
    fun shareVideo(@Body body: ApiVideo.ReqShare): Call<Nope>

    @GET("/getvideocommentslist/{uid}/{fastkey}/{videoid}/")
    fun comments(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String,
            @Path("videoid") videoid: Long
    ): Call<ApiComment>

    @POST("/videocomments/")
    fun createComment(@Body body: ApiCreateComment.Req): Call<ApiCreateComment>

    @POST("/commentslike/")
    fun likeComment(@Body body: ApiLike.ReqComment): Call<Nope>

    @GET("/getfollowlist/{uid}/{fastkey}/")
    fun followList(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String
    ): Call<ApiFollow>

    @GET("/getfanslist/{uid}/{fastkey}/")
    fun fanList(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String
    ): Call<ApiFans>

    @GET("/getuserrank/{uid}/{fastkey}/{type}/{timestemp}/{page}/{size}/")
    fun ranksOfUser(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String,
            @Path("type") type: String,
            @Path("timestemp") time: Long,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiRank>

    @GET("/getusercount/{uid}/")
    fun getUserCount(
            @Path("uid") uid: Long
    ): Call<ApiUserCount>

    @GET("/getuservideolist/{uid}/{page}/{size}/")
    fun videosOfUser(
            @Path("uid") uid: Long,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiVideo>

    @GET("/getuserlikevideolist/{uid}/{page}/{size}/")
    fun videosOfLike(
            @Path("uid") uid: Long,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiVideo>

    @GET("/getindexmessage/{type}/")
    fun adMsg(@Path("type") type: Int): Call<ApiAdMsg>

    @GET("/getadconfig/{type}/")
    fun ad(@Path("type") type: Int): Call<ApiAd>

    @GET("/commentstouser/{uid}/{fastkey}/{page}/{size}/")
    fun commentList(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiCommentList>

    @GET("/commentsatuser/{uid}/{fastkey}/{page}/{size}/")
    fun atList(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiAtList>

    @GET("/userlikeme/{uid}/{fastkey}/{page}/{size}/")
    fun likeList(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiLikeList>

    @POST("/deletevideocomments/")
    fun deleteComment(@Body body: ApiComment.ReqDelete): Call<Nope>

    @GET("/getvideobyid/{uid}/{fastkey}/{videoid}/")
    fun videoById(
            @Path("uid") uId: Long,
            @Path("fastkey") token: String,
            @Path("videoid") videoId: Long
    ): Call<ApiVideoById>

    @GET("/searchuser/{keyword}/")
    fun searchUsers(@Path("keyword") keyword: String): Call<ApiAtUser>

    @POST("/postvideoplayduration/")
    fun playDuration(@Body body: ApiDurationReq): Call<Nope>

    @POST("/postSearchVideoUser/{uid}/{keyword}/{page}/{size}")
    fun searchVideo(
            @Path("uid") uId: Long,
            @Path("keyword") keyword: String,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiVideo>

    @GET("/getHotKeyTopN")
    fun getHotKey(): Call<ApiHotKey>

    @GET("/getVersion/{platform}")
    fun getVersion(@Path("platform") platform: Int): Call<ApiVersion>

    @GET("/getShareUrl")
    fun getDownLoadUrl(): Call<ApiDownLoadUrl>

    //-------⬇⬇⬇⬇⬇⬇⬇⬇---长视频相关接口-----⬇⬇⬇⬇⬇⬇⬇-----//

    @GET("/getRotationChart/{platform}/")
    fun getBanner(
            @Path("platform") platform: Int,
    ): Call<ApiMovieBanner>

    @GET("/getMovieHotKeyTopN/")
    fun getMovieHotKey(): Call<ApiMovieHotKey>

    @POST("/postSearchMovie/")
    fun searchMovie(@Body bode: ApiMovie.ReqSearch): Call<ApiMovie>

    @POST("/postRecordMovieHotKey/{mid}/")
    fun playSearchMovie(
            @Path("mid") mid: Int
    ): Call<Nope>

    //-------⬆⬆⬆⬆⬆⬆⬆⬆----长视频相关接口----⬆⬆⬆⬆⬆⬆⬆-----//
}