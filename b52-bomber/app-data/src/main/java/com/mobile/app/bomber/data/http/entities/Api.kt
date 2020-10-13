package com.mobile.app.bomber.data.http.entities

import com.mobile.guava.data.nullSafe
import com.mobile.guava.jvm.math.MathUtils
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

const val HOST_TAG_TEST = 0 //测试服
const val HOST_TAG_DEV = 1 //开发服
const val HOST_TAG_RELEASE = 2 //正式服

const val HOST_TAG = HOST_TAG_TEST

//测试环境  //http://117.50.119.233   |http://119.28.18.13
const val HOST_TEST = "http://api.zomppga.tokyo:444"

//开发环境
const val HOST_DEV = "http://192.168.2.120:"

//正式环境
const val HOST_RELEASE = "https://weiseapi.zkangcn.com"
const val HOST_RELEASE_UPLOAD = "https://weisesp.pumiaox2.com"

//视频解码地址
const val DECODE_URL = "${HOST_TEST}8080" //不同环境下需要修改

@JsonClass(generateAdapter = true)
data class Nope(@Json(name = "RetCode") val code: Int)

@JsonClass(generateAdapter = true)
data class ApiToken(
        @Json(name = "RetCode") val code: Int,
        @Json(name = "uid") val uid: Long?,
        @Json(name = "fastkey") val token: String?
) {
    @JsonClass(generateAdapter = true)
    data class AReq(
            @Json(name = "channel") val channelId: Int,
            @Json(name = "logintype") val loginType: Int,
            @Json(name = "deviceid") val deviceId: String,
            @Json(name = "phonenum") val telephone: String,
            @Json(name = "verifycode") val verifyCode: String
    )

    @JsonClass(generateAdapter = true)
    data class Req(
            @Json(name = "uid") val uid: Long,
            @Json(name = "fastkey") val token: String
    )
}

@JsonClass(generateAdapter = true)
data class ApiLike(
        @Json(name = "RetCode") val code: Int,
        @Json(name = "Comments") val comments: List<ApiComment.Comment>?
) {

    @JsonClass(generateAdapter = true)
    data class Req(
            @Json(name = "uid") val uid: Long,
            @Json(name = "fastkey") val token: String,
            @Json(name = "videoid") val videoId: Long,
            @Json(name = "like") val likeOrNot: Long
    )

    @JsonClass(generateAdapter = true)
    data class ReqComment(
            @Json(name = "uid") val uid: Long,
            @Json(name = "fastkey") val token: String,
            @Json(name = "commentsid") val commentId: Long,
            @Json(name = "like") val likeOrNot: Long
    )
}

@JsonClass(generateAdapter = true)
data class ApiVideo(
        @Json(name = "RetCode") val code: Int,
        @Json(name = "totalcount") val totalCount: Int,
        @Json(name = "next") val nextPage: Int,
        @Json(name = "totalpage") val totalPage: Int,
        @Json(name = "VideoList") val videos: List<Video>?
) {

    @JsonClass(generateAdapter = true)
    data class ReqShare(
            @Json(name = "videoid") val videoId: Long
    )

    @JsonClass(generateAdapter = true)
    data class ReqPlay(
            @Json(name = "videoid") val videoId: Long,
            @Json(name = "uid") val uId: Long,
            @Json(name = "channelid") val channelId: Int,
            @Json(name = "deviceid") val deviceId: String
    )

    @JsonClass(generateAdapter = true)
    data class Video(
            @Json(name = "VideoId") val videoId: Long,
            @Json(name = "VideoURL") val videoURL: String, //如果需要使用，需要引用decodeVideoUrl()
            @Json(name = "Owner") var owner: Long,
            @Json(name = "UploadTm") val uploadTime: Long,
            @Json(name = "Comments") val commentCount: Int,
            @Json(name = "Share") val shareCount: Int,
            @Json(name = "Desc") val desc: String?,
            @Json(name = "Title") val title: String?,
            @Json(name = "Cover") val coverImageUrl: String?,
            @Json(name = "Like") var likeCount: Int,
            @Json(name = "Isfollowed") var isFollowing: Boolean,
            @Json(name = "IsLiked") var isLiking: Boolean,
            @Json(name = "PlayCount") var playCount: Int,
            @Json(name = "Distance") var distance: Long?,
            @Json(name = "Label") var label: String?,
            @Json(name = "Username") var username: String,
            @Json(name = "Pic") var profile: String?,
            @Json(name = "Aid") var adId: Long?,
            @Json(name = "Addownloadurl") var adUrl: String?
    ) : Serializable {

        fun distanceText(): String {
            val value = distance.nullSafe()
            if (value < 1000L) {
                return "<1km"
            }
            if (value > 100_000L) {
                return " >100km"
            }
            return MathUtils.prettyDouble(MathUtils.divide(value.toDouble(), 1000.00), 1) + "km"
        }

        fun moment(): String {
            return title.nullSafe() + label.nullSafe()
        }

        fun decodeVideoUrl(): String {
            // return "${DECODE_URL}/videodecode?videourl=${videoURL}&download=0"
            return videoURL

        }

        fun downloadVideoUrl(): String {
            // return "${DECODE_URL}/videodecode?videourl=${videoURL}"
            return ""
        }
    }
}

@JsonClass(generateAdapter = true)
data class ApiUser(
        @Json(name = "RetCode") val code: Int,
        @Json(name = "Uid") val uid: Long,
        @Json(name = "Sex") val gender: Int?,
        @Json(name = "Username") val username: String,
        @Json(name = "Pic") val Pic: String?,
        @Json(name = "Sign") val sign: String?,
        @Json(name = "Birthday") val birthday: String?,
        @Json(name = "Wechat") val wechat: String?
) {
    @JsonClass(generateAdapter = true)
    data class BirthdayReq(
            @Json(name = "uid") val uid: Long,
            @Json(name = "fastkey") val token: String,
            @Json(name = "birthday") val birthday: String
    )

    @JsonClass(generateAdapter = true)
    data class GenderReq(
            @Json(name = "uid") val uid: Long,
            @Json(name = "fastkey") val token: String,
            @Json(name = "sex") val gender: Int
    )

    @JsonClass(generateAdapter = true)
    data class SignReq(
            @Json(name = "uid") val uid: Long,
            @Json(name = "fastkey") val token: String,
            @Json(name = "sign") val sign: String
    )

    @JsonClass(generateAdapter = true)
    data class NicknameReq(
            @Json(name = "uid") val uid: Long,
            @Json(name = "fastkey") val token: String,
            @Json(name = "username") val nickName: String
    )

    @JsonClass(generateAdapter = true)
    data class WechatReq(
            @Json(name = "uid") val uid: Long,
            @Json(name = "fastkey") val token: String,
            @Json(name = "wechat") val wechatID: String
    )
}

@JsonClass(generateAdapter = true)
data class ApiFollow(
        @Json(name = "RetCode") val code: Int,
        @Json(name = "FollwedList") val follows: List<Follow>?
) {
    @JsonClass(generateAdapter = true)
    data class Req(
            @Json(name = "uid") val uid: Long,
            @Json(name = "fastkey") val token: String,
            @Json(name = "followuid") val targetUserId: Long,
            @Json(name = "unfollow") val notFollowing: Boolean
    )

    @JsonClass(generateAdapter = true)
    data class Follow(
            @Json(name = "Id") val id: Long,
            @Json(name = "Followuid") val followUid: Long,
            @Json(name = "Followtm") val followTime: Long,
            @Json(name = "Username") val username: String,
            @Json(name = "Pic") val profile: String?,
            @Json(name = "Sex") val gender: Int?,
            @Json(name = "Sign") val sign: String?,
            @Json(name = "Isfollowed") var isFollowing: Boolean?
    ) {
        fun toUser(): ApiAtUser.User {
            return ApiAtUser.User(followUid, gender, username, profile, sign, "", "")
        }
    }
}

@JsonClass(generateAdapter = true)
data class ApiIsFollowing(
        @Json(name = "RetCode") val code: Int,
        @Json(name = "isfollowed") val isFollowed: Boolean
)

@JsonClass(generateAdapter = true)
data class ApiFile(
        @Json(name = "retcode") val code: Int,
        @Json(name = "retmsg") val msg: String,
        @Json(name = "domain") val domain: String,
        @Json(name = "md5") val md5: String,
        @Json(name = "mtime") val time: Long,
        @Json(name = "path") val path: String,
        @Json(name = "scene") val scene: String,
        @Json(name = "scenes") val scenes: String,
        @Json(name = "size") val size: Int,
        @Json(name = "src") val src: String,
        @Json(name = "url") val url: String
) {
    @JsonClass(generateAdapter = true)
    data class ReqPicture(
            @Json(name = "uid") val uid: Long,
            @Json(name = "fastkey") val token: String,
            @Json(name = "headPic") val url: String
    )

    @JsonClass(generateAdapter = true)
    data class ReqVideo(
            @Json(name = "uid") val uid: Long,
            @Json(name = "owner") val owner: Long,
            @Json(name = "fastkey") val token: String,
            @Json(name = "videourl") val url: String,
            @Json(name = "cover") val coverUrl: String,
            @Json(name = "desc") val desc: String,
            @Json(name = "label") val label: String,
            @Json(name = "lat") val latitude: Double,
            @Json(name = "lng") val lng: Double
    )
}

@JsonClass(generateAdapter = true)
data class ApiCreateComment(
        @Json(name = "RetCode") val code: Int,
        @Json(name = "Commentid") val id: Long
) {

    @JsonClass(generateAdapter = true)
    data class Req(
            @Json(name = "uid") val uid: Long,
            @Json(name = "fastkey") val token: String,
            @Json(name = "videoid") val videoId: Long,
            @Json(name = "targetid") val toCommentId: Long,
            @Json(name = "targetuid") val toUserId: Long,
            @Json(name = "comments") val content: String,
            @Json(name = "atuids") val at: String
    )
}

@JsonClass(generateAdapter = true)
data class ApiComment(
        @Json(name = "RetCode") val code: Int,
        @Json(name = "Comments") val comments: List<Comment>?
) {

    @JsonClass(generateAdapter = true)
    data class ReqDelete(
            @Json(name = "uid") val uid: Long,
            @Json(name = "fastkey") val token: String,
            @Json(name = "commentid") val commentId: Long,
            @Json(name = "videoid") val videoId: Long
    )

    @JsonClass(generateAdapter = true)
    data class Comment(
            @Json(name = "uid") val uid: Long,
            @Json(name = "videoid") val videoId: Long,
            @Json(name = "id") var id: Long,
            @Json(name = "like") var likeCount: Int,
            @Json(name = "commenttm") val time: Long,
            @Json(name = "username") val username: String,
            @Json(name = "pic") val pic: String?,
            @Json(name = "comments") val content: String,
            @Json(name = "targetid") val toCommendId: Long,
            @Json(name = "targetuid") val toUserId: Long,
            @Json(name = "targetuname") val toUsername: String,
            @Json(name = "targetupic") val toPic: String?,
            @Json(name = "IsLiked") var isLiking: Boolean,
            @Json(name = "atlist") val at: List<At>?,
            @Json(name = "Children") val children: List<Comment>?
    ) {

        val realContent: String by lazy {
            var str = content
            atTexts.forEach {
                str = str.replace(it, "")
            }
            return@lazy str
        }

        val atTexts: List<String> by lazy {
            at.orEmpty().map { "@" + it.username }
        }

        val replayText: String by lazy {
            if (toUsername.isEmpty()) {
                ""
            } else {
                "回复${toUsername} "
            }
        }

        fun recursiveChildren(comment: Comment): List<Comment> {
            val list = ArrayList<Comment>()
            comment.children.orEmpty().forEach {
                list.add(it)
                list.addAll(recursiveChildren(it))
            }
            return list
        }
    }

    @JsonClass(generateAdapter = true)
    data class At(
            @Json(name = "uid") val uid: Long,
            @Json(name = "username") val username: String,
            @Json(name = "pic") val pic: String?
    )
}


@JsonClass(generateAdapter = true)
data class ApiRank(
        @Json(name = "RetCode") val code: Int,
        @Json(name = "totalcount") val totalCount: Int,
        @Json(name = "next") val nextPage: Int,
        @Json(name = "totalpage") val totalPage: Int,
        @Json(name = "Data") val ranks: List<Rank>?
) {
    @JsonClass(generateAdapter = true)
    data class Rank(
            @Json(name = "uid") val uId: Int,
            @Json(name = "num") val num: Float,
            @Json(name = "username") val username: String,
            @Json(name = "pic") val picUrl: String?,
            @Json(name = "isfollowed") var isFollowing: Boolean
    )
}

@JsonClass(generateAdapter = true)
data class ApiUserCount(
        @Json(name = "RetCode") val code: Int,
        @Json(name = "likecount") val likeCount: Int,
        @Json(name = "fanscount") val fansCount: Int,
        @Json(name = "followcount") val followCount: Int
)

@JsonClass(generateAdapter = true)
data class ApiFans(
        @Json(name = "RetCode") val code: Int,
        @Json(name = "FansList") val follows: List<ApiFollow.Follow>?
)

@JsonClass(generateAdapter = true)
data class ApiAdMsg(
        @Json(name = "RetCode") val code: Int,
        @Json(name = "id") val id: Long,
        @Json(name = "onlinestatus") val onlineStatus: Int,
        @Json(name = "content") val content: String,
        @Json(name = "title") val title: String,
        @Json(name = "linkurl") val url: String,
        @Json(name = "msgtype") val type: Int
)

@JsonClass(generateAdapter = true)
data class ApiAd(
        @Json(name = "RetCode") val code: Int,
        @Json(name = "id") val id: Long,
        @Json(name = "imageurl") val image: String,
        @Json(name = "linkurl") val url: String,
        @Json(name = "tyep") val type: Int,
        @Json(name = "weight") val weight: Int,
        @Json(name = "staytime") val startTime: Long
)

@JsonClass(generateAdapter = true)
data class ApiAtList(
        @Json(name = "RetCode") val code: Int,
        @Json(name = "totalcount") val totalCount: Int,
        @Json(name = "next") val nextPage: Int,
        @Json(name = "totalpage") val totalPage: Int,
        @Json(name = "comments") val items: List<Item>?
) {

    @JsonClass(generateAdapter = true)
    data class Item(
            @Json(name = "Id") val id: Long,
            @Json(name = "Uid") val uId: Long,
            @Json(name = "Commentsid") val commentsId: Long,
            @Json(name = "Atuid") val atUserId: Long,
            @Json(name = "Attm") val time: Int,
            @Json(name = "Username") var username: String,
            @Json(name = "Pic") var profile: String?,
            @Json(name = "Videoid") val videoId: Long,
            @Json(name = "Cover") val videoImageUrl: String?,
            @Json(name = "Comments") var content: String
    )
}

@JsonClass(generateAdapter = true)
data class ApiLikeList(
        @Json(name = "RetCode") val code: Int,
        @Json(name = "totalcount") val totalCount: Int,
        @Json(name = "next") val nextPage: Int,
        @Json(name = "totalpage") val totalPage: Int,
        @Json(name = "Data") val items: List<Item>?
) {

    @JsonClass(generateAdapter = true)
    data class Item(
            @Json(name = "Videoid") val videoId: Long,
            @Json(name = "Commentid") val commentId: Long,
            @Json(name = "Cover") val videoImage: String?,
            @Json(name = "Comments") val comments: String?,
            @Json(name = "Liketm") val Liketm: Long?,
            @Json(name = "Userlist") val at: List<UserLikelist>?
    )

    @JsonClass(generateAdapter = true)
    data class UserLikelist(
            @Json(name = "Uid") val uId: Long,
            @Json(name = "Username") val username: String,
            @Json(name = "Pic") val profile: String?
    )
}

@JsonClass(generateAdapter = true)
data class ApiCommentList(
        @Json(name = "RetCode") val code: Int,
        @Json(name = "totalcount") val totalCount: Int,
        @Json(name = "next") val nextPage: Int,
        @Json(name = "totalpage") val totalPage: Int,
        @Json(name = "comments") val items: List<Item>?
) {

    @JsonClass(generateAdapter = true)
    data class Item(
            @Json(name = "Id") val id: Long,
            @Json(name = "Uid") val uId: Long,
            @Json(name = "Like") val likeCount: Long,
            @Json(name = "CommentTm") val time: Long,
            @Json(name = "Comments") val content: String,
            @Json(name = "Videoid") val videoId: Long,
            @Json(name = "Targetid") val toCommendId: Long,
            @Json(name = "Targetuid") val toUserId: Long,
            @Json(name = "Cover") val videoImageUrl: String?,
            @Json(name = "Username") var username: String,
            @Json(name = "Atuids") var at: String,
            @Json(name = "Pic") var profile: String?
    )
}

@JsonClass(generateAdapter = true)
data class ApiVideoById(
        @Json(name = "RetCode") val code: Int,
        @Json(name = "Video") val video: ApiVideo.Video
)

@JsonClass(generateAdapter = true)
data class ApiAtUser(
        @Json(name = "RetCode") val code: Int,
        @Json(name = "Data") val users: List<User>?
) {

    @JsonClass(generateAdapter = true)
    data class User(
            @Json(name = "Uid") val uid: Long,
            @Json(name = "Sex") val gender: Int?,
            @Json(name = "Username") val username: String,
            @Json(name = "Pic") val profile: String?,
            @Json(name = "Sign") val sign: String?,
            @Json(name = "Birthday") val birthday: String?,
            @Json(name = "Wechat") val wechat: String?
    )
}

@JsonClass(generateAdapter = true)
data class ApiDurationReq(
        @Json(name = "uid") val uid: Long,
        @Json(name = "deviceId") val deviceId: String,
        @Json(name = "channelId") val channelId: Int,
        @Json(name = "videoId") val videoId: Long,
        @Json(name = "duration") val duration: Long?,
        @Json(name = "aid") val aid: Long
)

@JsonClass(generateAdapter = true)
data class ApipostUserMsg(
        @Json(name = "msgtype") val msgtype: Int,
        @Json(name = "timestemp") val timestemp: Int,
        @Json(name = "uid") val uid: Long,
        @Json(name = "fastkey") val fastkey: String,
)

@JsonClass(generateAdapter = true)
data class ApiHotKey(
        @Json(name = "RetCode") val code: Int,
        @Json(name = "Desc") val desc: String,
        @Json(name = "KeyWords") val KeyWords: List<String>?
)

@JsonClass(generateAdapter = true)
data class ApiVersion(
        @Json(name = "RetCode") val code: Int,
        @Json(name = "Desc") val desc: String,
        @Json(name = "Versions") val versions: Version,
) {
    @JsonClass(generateAdapter = true)
    data class Version(
            @Json(name = "Id") val Id: Int,
            @Json(name = "Type") val type: Int,
            @Json(name = "VersionName") val versionName: String,
            @Json(name = "Force") val force: Int,
            @Json(name = "VersionCode") val versionCode: String,
            @Json(name = "DownloadUrl") val downloadUrl: String,
            @Json(name = "Desc") val desc: String,
            @Json(name = "AddTime") val addTime: Long
    )
}

@JsonClass(generateAdapter = true)
data class ApiDownLoadUrl(
        @Json(name = "RetCode") val code: Int,
        @Json(name = "desc") val desc: String,
        @Json(name = "shareUrl") val downloadUrl: String
)


@JsonClass(generateAdapter = true)
data class ApiFixedad(
        @Json(name = "RetCode") val code: Int,
        @Json(name = "Desc") val desc: String,
        @Json(name = "FixedadObj") val fixedadObj: FixedadObj
){
    @JsonClass(generateAdapter = true)
    data class FixedadObj(
            @Json(name = "title") val Id: String,
            @Json(name = "platForm") val type: Int,
            @Json(name = "url") val url: String,
            @Json(name = "resolutionData") val resolutionData: ResolutionData

    )
    @JsonClass(generateAdapter = true)
    data class ResolutionData(
            @Json(name = "sixteen") val sixteen: String,
            @Json(name = "eighteen") val eighteen: String,
            @Json(name = "twentyOne") val twentyOne: String
    )
}


//-------⬇⬇⬇⬇⬇⬇⬇⬇---长视频相关-----⬇⬇⬇⬇⬇⬇⬇-----//

@JsonClass(generateAdapter = true)
data class ApiMovieBanner(
        @Json(name = "RetCode") val code: Int,
        @Json(name = "Desc") val desc: String,
        @Json(name = "RotationCharts") val banner: List<Banner>?
) {
    @JsonClass(generateAdapter = true)
    data class Banner(
            @Json(name = "Id") val code: Long,
            @Json(name = "Title") val title: String,
            @Json(name = "ImgUrl") val imgUrl: String,
            @Json(name = "MovieId") val movieId: Long,
            @Json(name = "MovieUrl") val movieUrl: String,
            @Json(name = "Tag") val tag: Int,
            @Json(name = "Platform") val platform: Int
    )
}

@JsonClass(generateAdapter = true)
data class ApiMovieHotKey(
        @Json(name = "RetCode") val code: Int,
        @Json(name = "Desc") val desc: String,
        @Json(name = "MovieHotKey") val hotKeys: List<MovieHotKey>?
) {
    @JsonClass(generateAdapter = true)
    data class MovieHotKey(
            @Json(name = "movieId") val movieId: Long,
            @Json(name = "name") val name: String,
            @Json(name = "searchCount") val searchCount: Int,
            @Json(name = "hotType") val hotType: Int,
            @Json(name = "MovieObj") val movie: ApiMovie.Movie
    )
}

@JsonClass(generateAdapter = true)
data class ApiMovie(
        @Json(name = "RetCode") val code: Int,
        @Json(name = "Desc") val desc: String,
        @Json(name = "totalpage") val totalPage: Int,
        @Json(name = "totalcount") val totalCount: Int,
        @Json(name = "next") val nextPage: Int,
        @Json(name = "MovieList") val movies: List<Movie>?
) {

    @JsonClass(generateAdapter = true)
    data class Movie(
            @Json(name = "Id") val id: Long,
            @Json(name = "MovieId") val movieId: Int,
            @Json(name = "MovieUrl") val movieUrl: String,
            @Json(name = "Downloadurl") val downloadUrl: String,
            @Json(name = "Uploadtm") val uploadTime: Long,
            @Json(name = "Duration") val duration: Long,
            @Json(name = "Recommend") val recommend: Int,
            @Json(name = "Like") val like: Int,
            @Json(name = "Playnum") val playNum: Long,
            @Json(name = "Comments") val comments: Long,
            @Json(name = "Share") val share: Int,
            @Json(name = "Follow") val follow: Int,
            @Json(name = "Download") val download: String,
            @Json(name = "Country") val country: Int,
            @Json(name = "Year") val year: Long,
            @Json(name = "Mosaic") val mosaic: Int,
            @Json(name = "Captions") val captions: Int,
            @Json(name = "Score") val score: Int,
            @Json(name = "Category") val category: String,
            @Json(name = "Desc") val desc: String,
            @Json(name = "Propaganda") val propaganda: String,
            @Json(name = "Name") val name: String,
            @Json(name = "Cover") val cover: String,
            @Json(name = "Isportait") val isPortait: Int,
            @Json(name = "Byuid") val byUid: Long,
    )

    @JsonClass(generateAdapter = true)
    data class ReqSearch(
            @Json(name = "keyword") val keyword: String,
            @Json(name = "page") val page: Int,
            @Json(name = "size") val size: Int
    )
}

@JsonClass(generateAdapter = true)
data class ApiUsermsg(
        @Json(name = "RetCode") val retCode: Int,
        @Json(name = "UserMsgObj") val items: List<Item>?
) {

    @JsonClass(generateAdapter = true)
    data class Item(
            @Json(name = "Atid") val atid: Int,
            @Json(name = "Commentid") val commentid: Int,
            @Json(name = "Commenttype") val commenttype: Int,
            @Json(name = "Content") val content: String,
            @Json(name = "Createtime") val createtime: Long,
            @Json(name = "Followtype") val followtype: Int,
            @Json(name = "Fromuid") val fromuid: Int,
            @Json(name = "Fromuserinfo") val fromuserinfo: List<Fromuserinfo>,
            @Json(name = "Giveliketype") val giveliketype: Int,
            @Json(name = "Id") val id: Int,
            @Json(name = "Isfollow") var isfollow: Int,
            @Json(name = "Msgtype") val msgtype: Int,
            @Json(name = "Uid") val uid: Int,
            @Json(name = "Videoid") val videoid: Int,
            @Json(name = "Cover") val cover: String
    ) {

        @JsonClass(generateAdapter = true)
        data class Fromuserinfo(
                @Json(name = "Name") val name: String,
                @Json(name = "Pic") val pic: String,
                @Json(name = "Uid") val uid: Int
        )
    }
}

//-------⬆⬆⬆⬆⬆⬆⬆⬆----长视频相关----⬆⬆⬆⬆⬆⬆⬆-----//
