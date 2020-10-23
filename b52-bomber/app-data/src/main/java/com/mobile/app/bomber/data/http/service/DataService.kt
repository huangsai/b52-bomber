package com.mobile.app.bomber.data.http.service

import com.mobile.app.bomber.data.http.entities.*
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DataService {

    @POST("/user/login/")
    fun login(@Body body: ApiToken.AReq): Call<ApiToken>

    @POST("/user/fastlogin/")
    fun fastLogin(@Body body: ApiToken.Req): Call<ApiToken>

    @GET("/user/getverifycode/{phonenum}/")
    fun verifyCode(@Path("phonenum") telephone: String): Call<Nope>

    @POST("/video/videolike/")
    fun likeVideo(@Body body: ApiLike.Req): Call<ApiLike>

    @GET("/video/getdistancevideolist/{uid}/{fastkey}/{lat}/{lng}/{page}/{size}/ ")
    fun videosOfNearby(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String,
            @Path("lat") lat: Double,
            @Path("lng") lng: Double,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiVideo>

    @GET("/video/getfollowvideolist/{uid}/{fastkey}/{page}/{size}/")
    fun videosOfFollow(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiVideo>

    @GET("/video/getvideolist/{uid}/{fastkey}/{label}/{sort}/{page}/{size}/")
    fun queryVideos(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String,
            @Path("label") label: String,
            @Path("sort") sort: String,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiVideo>

    @GET("/video/getvideorecommend/{uid}/{fastkey}/{page}/{size}/")
    fun queryCommendVideos(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiVideo>

    @GET("/user/getuserinfo/{uid}/")
    fun user(@Path("uid") uid: Long): Call<ApiUser>

    @POST("/user/modifyuserInfo/")
    fun updateBirthday(@Body body: ApiUser.BirthdayReq): Call<Nope>

    @POST("/user/modifyuserInfo/")
    fun updateGender(@Body body: ApiUser.GenderReq): Call<Nope>

    @POST("/user/modifyuserInfo/")
    fun updateSign(@Body body: ApiUser.SignReq): Call<Nope>

    @POST("/user/modifyuserInfo/")
    fun updateNickname(@Body body: ApiUser.NicknameReq): Call<Nope>

    @POST("/user/modifyuserInfo/")
    fun updateWechat(@Body body: ApiUser.WechatReq): Call<Nope>

    @POST("/user/followone/")
    fun follow(@Body body: ApiFollow.Req): Call<ApiFollow>

    @GET("/user/isfollowed/{uid}/{fastkey}/{userid}/")
    fun isFollowing(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String,
            @Path("userid") targetUid: Long
    ): Call<ApiIsFollowing>

    @POST("/group1/upload")
    fun uploadFile(@Body body: RequestBody): Call<ApiFile>

    @POST("/user/changeheadpic/")
    fun syncPicture(@Body body: ApiFile.ReqPicture): Call<Nope>

    @POST("/video/videoupoload/")
    fun syncVideo(@Body body: ApiFile.ReqVideo): Call<Nope>

    @POST("/video/videoplay/")
    fun playVideo(@Body body: ApiVideo.ReqPlay): Call<Nope>

    @POST("/video/sharevideo/")
    fun shareVideo(@Body body: ApiVideo.ReqShare): Call<Nope>

    @GET("/video/getvideocommentslist/{uid}/{fastkey}/{videoid}/")
    fun comments(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String,
            @Path("videoid") videoid: Long
    ): Call<ApiComment>

    @POST("/video/videocomments/")
    fun createComment(@Body body: ApiCreateComment.Req): Call<ApiCreateComment>

    @POST("/video/commentslike/")
    fun likeComment(@Body body: ApiLike.ReqComment): Call<Nope>

    @GET("/user/getfollowlist/{uid}/{fastkey}/")
    fun followList(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String
    ): Call<ApiFollow>

    @GET("/user/getfanslist/{uid}/{fastkey}/")
    fun fanList(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String
    ): Call<ApiFans>

    @GET("/user/getuserrank/{uid}/{fastkey}/{type}/{timestemp}/{page}/{size}/")
    fun ranksOfUser(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String,
            @Path("type") type: String,
            @Path("timestemp") time: Long,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiRank>

    @GET("/user/getusercount/{uid}/")
    fun getUserCount(
            @Path("uid") uid: Long
    ): Call<ApiUserCount>

    @GET("/video/getuservideolist/{uid}/{page}/{size}/")
    fun videosOfUser(
            @Path("uid") uid: Long,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiVideo>

    @GET("/video/getuserlikevideolist/{uid}/{page}/{size}/")
    fun videosOfLike(
            @Path("uid") uid: Long,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiVideo>

    @GET("/sys/getindexmessage/{type}/")
    fun adMsg(@Path("type") type: Int): Call<ApiAdMsg>

    @GET("/sys/getadconfig/{type}/")
    fun ad(@Path("type") type: Int): Call<ApiAd>

    @GET("/video/commentstouser/{uid}/{fastkey}/{page}/{size}/")
    fun commentList(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiCommentList>

    @GET("/video/commentsatuser/{uid}/{fastkey}/{page}/{size}/")
    fun atList(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiAtList>

    @GET("/video/userlikeme/{uid}/{fastkey}/{page}/{size}/")
    fun likeList(
            @Path("uid") uid: Long,
            @Path("fastkey") token: String,
            @Path("page") page: Int,
            @Path("size") pageSize: Int
    ): Call<ApiLikeList>

    //获取用户消息
    @POST("/user/postusermsg")
    fun postUserMsg(@Body body: ApipostUserMsg): Call<ApiUsermsg>

    @POST("/video/deletevideocomments/")
    fun deleteComment(@Body body: ApiComment.ReqDelete): Call<Nope>

    @GET("/video/getvideobyid/{uid}/{fastkey}/{videoid}/")
    fun videoById(
            @Path("uid") uId: Long,
            @Path("fastkey") token: String,
            @Path("videoid") videoId: Long
    ): Call<ApiVideoById>

    @GET("/user/searchuser/{keyword}/")
    fun searchTikUsers(@Path("keyword") keyword: String): Call<ApiAtUser>

    @POST("/video/postvideoplayduration/")
    fun playDuration(@Body body: ApiDurationReq): Call<Nope>

    @GET("/video/user/searchuser/{keyword}/")
    fun searchUsers(@Path("keyword") keyword: String): Call<ApiAtUser>


    @POST("/video/postSearchVideoUser/{uid}/{keyword}/{page}/{size}")
    fun searchVideo(
            @Path("uid") uId: Long,
            @Path("keyword") keyword: String,
            @Path("page") page: Int,
            @Path("size") pageSize: Int,
            @Body body: Any
    ): Call<ApiVideo>

    @GET("/video/getHotKeyTopN")
    fun getHotKey(): Call<ApiHotKey>

    @GET("/video/getTags")
    fun getTags(): Call<ApiTags>

    @GET("/sys/getVersion/{platform}/")
    fun getVersion(@Path("platform") platform: Int): Call<ApiVersion>

    @GET("/sys/getShareUrl/")
    fun getDownLoadUrl(): Call<ApiDownLoadUrl>

    @GET("/sys/getFixedad/")
    fun getFixedad(): Call<ApiFixedad>

    //-------⬇⬇⬇⬇⬇⬇⬇⬇---长视频相关接口-----⬇⬇⬇⬇⬇⬇⬇-----//

    @GET("/video/getRotationChart/{platform}/")
    fun getBanner(
            @Path("platform") platform: Int,
    ): Call<ApiMovieBanner>

    @GET("/video/getMovieHotKeyTopN")
    fun getMovieHotKey(): Call<ApiMovieHotKey>

    @POST("/video/postSearchMovie/")
    fun searchMovie(@Body bode: ApiMovie.ReqSearch): Call<ApiMovie>

    @POST("/video/postRecordMovieHotKey/{mid}/")
    fun playSearchMovie(
            @Path("mid") mid: Int
    ): Call<Nope>

    @GET("/video/getMovieCategory")
    fun getMovieLabel(): Call<ApiMovieLabel>

    @POST("/video/postMovieListByCategory")
    fun getMovieListByLabel(@Body bode: ApiMovie.ReqLabel): Call<ApiMovie>

    @GET("/video/getMovieRecomment/{uid}/")
    fun getMovieListRecommend(@Path("uid") uid: Long): Call<ApiMovie>

    @GET("/video/getMoviePlayDetailById/{mid}/{uid}/{fastKey}/")
    fun getMovieDetail(
            @Path("mid") movieId: Long,
            @Path("uid") uId: Long,
            @Path("fastKey") fastKey: String
    ): Call<ApiMovieDetail>

    //点赞
    @POST("/video/postMovieLike/")
    fun postMovieLike(@Body bode: ApiMovieId): Call<Nope>

    //收藏
    @POST("/video/submitMovieCollection/")
    fun postMovieCollection(@Body bode: ApiMovieCollection): Call<Nope>

    @POST("/video/getMovieHistory/")
    fun getMovieHistory(@Body bode: ApiMovieHistory.Req): Call<ApiMovieHistory>

    @GET("/video/getGuessULikeMovieList/{uid}/")
    fun getGuessULikeMovieList(@Path("uid") uid: Long): Call<ApiMovie>

    //获取用户收藏的电影列表
    @POST("/video/getMovieCollectionList/")
    fun getMovieCollectionList(@Body bode: ApiCollectionReq): Call<ApiMovieCollectionList>

    //上报影视播放次数
    @POST("/video/postMoviePlayNum/")
    fun postMoviePlayNum(@Body bode: ApiNumReq): Call<Nope>

    //上报影视播放时长
    @POST("/video/postMoviePlayDurationRecord/")
    fun postMoviePlayDurationRecord(@Body bode: ApiDurationRecordReq): Call<Nope>
    //-------⬆⬆⬆⬆⬆⬆⬆⬆----长视频相关接口----⬆⬆⬆⬆⬆⬆⬆-----//


}