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

//测试环境  //http://117.50.119.233   |http://119.28.18.13  http://api.zomppga.tokyo:444
const val HOST_TEST = "http://172.31.9.97/"

//开发环境
const val HOST_DEV = "http://192.168.2.120:"

//正式环境
const val HOST_RELEASE = "https://weiseapi.zkangcn.com"
const val HOST_RELEASE_UPLOAD = "https://weisesp.pumiaox2.com"

//视频解码地址
const val DECODE_URL = "${HOST_TEST}8080" //不同环境下需要修改

@JsonClass(generateAdapter = true)
data class Nope(@Json(name = "retCode") val code: Int)

@JsonClass(generateAdapter = true)
data class ApiToken(
        @Json(name = "retCode") val code: Int,
        @Json(name = "uid") val uid: Long?,
        @Json(name = "fastKey") val token: String?
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
        @Json(name = "retCode") val code: Int,
        @Json(name = "comments") val comments: List<ApiComment.Comment>?
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
        @Json(name = "retCode") val code: Int,
        @Json(name = "totalCount") val totalCount: Int,
        @Json(name = "next") val nextPage: Int,
        @Json(name = "totalPage") val totalPage: Int,
        @Json(name = "videoList") val videos: List<Video>?
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
            @Json(name = "videoId") val videoId: Long,
            @Json(name = "videoUrl") val videoURL: String, //如果需要使用，需要引用decodeVideoUrl()
            @Json(name = "owner") var owner: Long,
            @Json(name = "uploadTm") val uploadTime: Long,
            @Json(name = "comments") val commentCount: Int,
            @Json(name = "share") val shareCount: Int,
            @Json(name = "desc") val desc: String?,
            @Json(name = "title") val title: String?,
            @Json(name = "cover") val coverImageUrl: String?,
            @Json(name = "like") var likeCount: Int,
            @Json(name = "isFollowed") var isFollowing: Boolean,
            @Json(name = "isLiked") var isLiking: Boolean,
            @Json(name = "playCount") var playCount: Int,
            @Json(name = "distance") var distance: Long?,
            @Json(name = "label") var label: String?,
            @Json(name = "username") var username: String,
            @Json(name = "pic") var profile: String?,
            @Json(name = "aid") var adId: Long?,
            @Json(name = "addownloadurl") var adUrl: String?
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
        @Json(name = "retCode") val code: Int,
        @Json(name = "uid") val uid: Long,
        @Json(name = "sex") val gender: Int?,
        @Json(name = "username") val username: String,
        @Json(name = "pic") val Pic: String?,
        @Json(name = "sign") val sign: String?,
        @Json(name = "birthday") val birthday: String?,
        @Json(name = "wechat") val wechat: String?
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
        @Json(name = "retCode") val code: Int,
        @Json(name = "follwedList") val follows: List<Follow>?
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
            @Json(name = "id") val id: Long,
            @Json(name = "followuid") val followUid: Long,
            @Json(name = "followtm") val followTime: Long,
            @Json(name = "username") val username: String,
            @Json(name = "pic") val profile: String?,
            @Json(name = "sex") val gender: Int?,
            @Json(name = "sign") val sign: String?,
            @Json(name = "Isfollowed") var isFollowing: Boolean?
    ) {
        fun toUser(): ApiAtUser.User {
            return ApiAtUser.User(followUid, gender, username, profile, sign, "", "")
        }
    }
}

@JsonClass(generateAdapter = true)
data class ApiIsFollowing(
        @Json(name = "retCode") val code: Int,
        @Json(name = "isFollowed") val isFollowed: Boolean
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
        @Json(name = "retCode") val code: Int,
        @Json(name = "commentid") val id: Long
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
        @Json(name = "retCode") val code: Int,
        @Json(name = "comments") val comments: List<Comment>?
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
            @Json(name = "isLiked") var isLiking: Boolean,
            @Json(name = "atlist") val at: List<At>?,
            @Json(name = "children") val children: List<Comment>?
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
        @Json(name = "retCode") val code: Int,
        @Json(name = "totalCount") val totalCount: Int,
        @Json(name = "next") val nextPage: Int,
        @Json(name = "totalPage") val totalPage: Int,
        @Json(name = "data") val ranks: List<Rank>?
) {
    @JsonClass(generateAdapter = true)
    data class Rank(
            @Json(name = "uid") val uId: Int,
            @Json(name = "num") val num: Float,
            @Json(name = "userName") val username: String,
            @Json(name = "pic") val picUrl: String?,
            @Json(name = "isFollowed") var isFollowing: Boolean
    )
}

@JsonClass(generateAdapter = true)
data class ApiUserCount(
        @Json(name = "retCode") val code: Int,
        @Json(name = "likeCount") val likeCount: Int,
        @Json(name = "fansCount") val fansCount: Int,
        @Json(name = "followCount") val followCount: Int
)

@JsonClass(generateAdapter = true)
data class ApiFans(
        @Json(name = "retCode") val code: Int,
        @Json(name = "fansList") val follows: List<ApiFollow.Follow>?
)

@JsonClass(generateAdapter = true)
data class ApiAdMsg(
        @Json(name = "retCode") val code: Int,
        @Json(name = "id") val id: Long,
        @Json(name = "onlinestatus") val onlineStatus: Int,
        @Json(name = "content") val content: String,
        @Json(name = "title") val title: String,
        @Json(name = "linkurl") val url: String,
        @Json(name = "msgtype") val type: Int
)

@JsonClass(generateAdapter = true)
data class ApiAd(
        @Json(name = "retCode") val code: Int,
        @Json(name = "id") val id: Long,
        @Json(name = "imageurl") val image: String,
        @Json(name = "linkurl") val url: String,
        @Json(name = "tyep") val type: Int,
        @Json(name = "weight") val weight: Int,
        @Json(name = "staytime") val startTime: Long
)

@JsonClass(generateAdapter = true)
data class ApiAtList(
        @Json(name = "retCode") val code: Int,
        @Json(name = "totalCount") val totalCount: Int,
        @Json(name = "next") val nextPage: Int,
        @Json(name = "totalPage") val totalPage: Int,
        @Json(name = "comments") val items: List<Item>?
) {

    @JsonClass(generateAdapter = true)
    data class Item(
            @Json(name = "id") val id: Long,
            @Json(name = "uid") val uId: Long,
            @Json(name = "commentsid") val commentsId: Long,
            @Json(name = "atuid") val atUserId: Long,
            @Json(name = "attm") val time: Int,
            @Json(name = "username") var username: String,
            @Json(name = "pic") var profile: String?,
            @Json(name = "videoid") val videoId: Long,
            @Json(name = "cover") val videoImageUrl: String?,
            @Json(name = "comments") var content: String
    )
}

@JsonClass(generateAdapter = true)
data class ApiLikeList(
        @Json(name = "retCode") val code: Int,
        @Json(name = "totalCount") val totalCount: Int,
        @Json(name = "next") val nextPage: Int,
        @Json(name = "totalPage") val totalPage: Int,
        @Json(name = "data") val items: List<Item>?
) {

    @JsonClass(generateAdapter = true)
    data class Item(
            @Json(name = "videoid") val videoId: Long,
            @Json(name = "commentid") val commentId: Long,
            @Json(name = "cover") val videoImage: String?,
            @Json(name = "comments") val comments: String?,
            @Json(name = "liketm") val Liketm: Long?,
            @Json(name = "userlist") val at: List<UserLikelist>?
    )

    @JsonClass(generateAdapter = true)
    data class UserLikelist(
            @Json(name = "uid") val uId: Long,
            @Json(name = "username") val username: String,
            @Json(name = "pic") val profile: String?
    )
}

@JsonClass(generateAdapter = true)
data class ApiCommentList(
        @Json(name = "retCode") val code: Int,
        @Json(name = "totalCount") val totalCount: Int,
        @Json(name = "next") val nextPage: Int,
        @Json(name = "totalPage") val totalPage: Int,
        @Json(name = "comments") val items: List<Item>?
) {

    @JsonClass(generateAdapter = true)
    data class Item(
            @Json(name = "id") val id: Long,
            @Json(name = "uid") val uId: Long,
            @Json(name = "like") val likeCount: Long,
            @Json(name = "commentTm") val time: Long,
            @Json(name = "comments") val content: String,
            @Json(name = "videoid") val videoId: Long,
            @Json(name = "targetid") val toCommendId: Long,
            @Json(name = "targetuid") val toUserId: Long,
            @Json(name = "cover") val videoImageUrl: String?,
            @Json(name = "username") var username: String,
            @Json(name = "atuids") var at: String,
            @Json(name = "pic") var profile: String?
    )
}

@JsonClass(generateAdapter = true)
data class ApiVideoById(
        @Json(name = "retCode") val code: Int,
        @Json(name = "video") val video: ApiVideo.Video
)

@JsonClass(generateAdapter = true)
data class ApiAtUser(
        @Json(name = "retCode") val code: Int,
        @Json(name = "data") val users: List<User>?
) {

    @JsonClass(generateAdapter = true)
    data class User(
            @Json(name = "uid") val uid: Long,
            @Json(name = "sex") val gender: Int?,
            @Json(name = "username") val username: String,
            @Json(name = "pic") val profile: String?,
            @Json(name = "sign") val sign: String?,
            @Json(name = "birthday") val birthday: String?,
            @Json(name = "wechat") val wechat: String?
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
        @Json(name = "retCode") val code: Int,
        @Json(name = "desc") val desc: String,
        @Json(name = "keyWords") val KeyWords: List<String>?
)


@JsonClass(generateAdapter = true)
data class ApiTags(
        @Json(name = "RetCode") val code: Int,
        @Json(name = "Desc") val desc: String,
        @Json(name = "Data") val data: List<String>?
)


@JsonClass(generateAdapter = true)
data class ApiVersion(
        @Json(name = "retCode") val code: Int,
        @Json(name = "desc") val desc: String,
        @Json(name = "versions") val versions: Version,
) {
    @JsonClass(generateAdapter = true)
    data class Version(
            @Json(name = "id") val Id: Int,
            @Json(name = "type") val type: Int,
            @Json(name = "versionName") val versionName: String,
            @Json(name = "force") val force: Int,
            @Json(name = "versionCode") val versionCode: String,
            @Json(name = "downloadUrl") val downloadUrl: String,
            @Json(name = "desc") val desc: String,
            @Json(name = "addTime") val addTime: Long
    )
}

@JsonClass(generateAdapter = true)
data class ApiDownLoadUrl(
        @Json(name = "retCode") val code: Int,
        @Json(name = "desc") val desc: String,
        @Json(name = "shareUrl") val downloadUrl: String
)


@JsonClass(generateAdapter = true)
data class ApiFixedad(
        @Json(name = "retCode") val code: Int,
        @Json(name = "desc") val desc: String,
        @Json(name = "fixedadObj") val fixedadObj: FixedadObj
) {
    @JsonClass(generateAdapter = true)
    data class FixedadObj(
            @Json(name = "title") val Id: String,
            @Json(name = "platForm") val type: Int,
            @Json(name = "url") val url: String,
            @Json(name = "resolutionData") val resolutionData: String

    )

    @JsonClass(generateAdapter = true)
    data class ResolutionData(
            @Json(name = "sixteen") val sixteen: String,
            @Json(name = "eighteen") val eighteen: String,
            @Json(name = "twentyOne") val twentyOne: String
    )
}

@JsonClass(generateAdapter = true)
data class ApiUsermsg(
        @Json(name = "retCode") val retCode: Int,
        @Json(name = "userMsgObj") val items: List<Item>?
) {

    @JsonClass(generateAdapter = true)
    data class Item(
            @Json(name = "atId") val atid: Int,
            @Json(name = "commentId") val commentid: Int,
            @Json(name = "commentType") val commenttype: Int,
            @Json(name = "content") val content: String,
            @Json(name = "createTime") val createtime: Long,
            @Json(name = "followType") val followtype: Int,
            @Json(name = "fromUid") val fromuid: Int,
            @Json(name = "fromUserInfo") val fromuserinfo: List<Fromuserinfo>,
            @Json(name = "giveLikeType") val giveliketype: Int,
            @Json(name = "id") val id: Int,
            @Json(name = "isFollow") var isfollow: Int,
            @Json(name = "msgType") val msgtype: Int,
            @Json(name = "uid") val uid: Int,
            @Json(name = "videoId") val videoid: Int,
            @Json(name = "cover") val cover: String
    ) {

        @JsonClass(generateAdapter = true)
        data class Fromuserinfo(
                @Json(name = "name") val name: String,
                @Json(name = "pic") val pic: String,
                @Json(name = "uid") val uid: Int
        )
    }
}

//-------⬇⬇⬇⬇⬇⬇⬇⬇---长视频相关-----⬇⬇⬇⬇⬇⬇⬇-----//

@JsonClass(generateAdapter = true)
data class ApiMovieBanner(
        @Json(name = "retCode") val code: Int,
        @Json(name = "desc") val desc: String,
        @Json(name = "rotationCharts") val banner: List<Banner>?
) {
    @JsonClass(generateAdapter = true)
    data class Banner(
            @Json(name = "id") val code: Long,
            @Json(name = "title") val title: String,
            @Json(name = "imgUrl") val imgUrl: String,
            @Json(name = "movieId") val movieId: Long,
            @Json(name = "movieUrl") val movieUrl: String,
            @Json(name = "tag") val tag: Int,
            @Json(name = "platform") val platform: Int
    )
}

@JsonClass(generateAdapter = true)
data class ApiMovieHotKey(
        @Json(name = "retCode") val code: Int,
        @Json(name = "desc") val desc: String,
        @Json(name = "movieHotKey") val hotKeys: List<MovieHotKey>?
) {
    @JsonClass(generateAdapter = true)
    data class MovieHotKey(
            @Json(name = "movieId") val movieId: Long,
            @Json(name = "name") val name: String,
            @Json(name = "searchCount") val searchCount: Int,
            @Json(name = "hotType") val hotType: Int,
            @Json(name = "movieObj") val movie: ApiMovie.Movie
    )
}

@JsonClass(generateAdapter = true)
data class ApiMovie(
        @Json(name = "retCode") val code: Int,
        @Json(name = "totalPage") val totalPage: Int,
        @Json(name = "totalCount") val totalCount: Int,
        @Json(name = "next") val nextPage: Int,
        @Json(name = "movieList") val movies: List<Movie>?
) {

    @JsonClass(generateAdapter = true)
    data class Movie(
            @Json(name = "id") val id: Long,
            @Json(name = "movieId") val movieId: Int,
            @Json(name = "movieUrl") val movieUrl: String,
            @Json(name = "downloadurl") val downloadUrl: String,
            @Json(name = "uploadtm") val uploadTime: Long,
            @Json(name = "duration") val duration: Long,
            @Json(name = "recommend") val recommend: Int,
            @Json(name = "like") val like: Int,
            @Json(name = "playnum") val playNum: Long,
            @Json(name = "comments") val comments: Long,
            @Json(name = "share") val share: Int,
            @Json(name = "follow") val follow: Int,
            @Json(name = "download") val download: String,
            @Json(name = "country") val country: Int,
            @Json(name = "year") val year: Long,
            @Json(name = "mosaic") val mosaic: Int,
            @Json(name = "captions") val captions: Int,
            @Json(name = "score") val score: Int,
            @Json(name = "category") val category: String,
            @Json(name = "desc") val desc: String,
            @Json(name = "propaganda") val propaganda: String,
            @Json(name = "name") val name: String,
            @Json(name = "cover") val cover: String,
            @Json(name = "isportait") val isPortait: Int,
            @Json(name = "byuid") val byUid: Long,
    )

    @JsonClass(generateAdapter = true)
    data class ReqSearch(
            @Json(name = "keyword") val keyword: String,
            @Json(name = "page") val page: Int,
            @Json(name = "size") val size: Int
    )

    @JsonClass(generateAdapter = true)
    data class ReqLabel(
            @Json(name = "uid") val uid: Long,
            @Json(name = "page") val page: Int,
            @Json(name = "size") val size: Int,
            @Json(name = "sort") val sort: String,
            @Json(name = "fastkey") val fastKey: String,
            @Json(name = "label") val label: String,
    )

    @JsonClass(generateAdapter = true)
    data class Ad(
            @Json(name = "img") val img: String,
            @Json(name = "url") val url: String
    )
}

@JsonClass(generateAdapter = true)
data class ApiMovieLabel(
        @Json(name = "retCode") val code: Int,
        @Json(name = "desc") val desc: String,
        @Json(name = "data") val labels: List<String>?
)

@JsonClass(generateAdapter = true)
data class ApiMovieDetail(
        @Json(name = "retCode") val code: Int,
        @Json(name = "desc") val desc: String,
        @Json(name = "movieObj") val movie: MovieObj,
        @Json(name = "ad") val ad: Ad
) {
    @JsonClass(generateAdapter = true)
    data class MovieObj(
            @Json(name = "movieId") val movieId: Int,
            @Json(name = "name") val name: String,
            @Json(name = "like") var likes: Int,
            @Json(name = "playNum") val playNum: Int,
            @Json(name = "movieUrl") val movieUrl: String,
            @Json(name = "isLike") var isLike: Int,
            @Json(name = "isCollection") var isCollection: Int
    )

    @JsonClass(generateAdapter = true)
    data class Ad(
            @Json(name = "img") val img: String,
            @Json(name = "url") val url: String,
            @Json(name = "desc") val desc: String,
            @Json(name = "title") val title: String
    )
}

@JsonClass(generateAdapter = true)
data class ApiMovieId(
        @Json(name = "uid") val uid: Long,
        @Json(name = "fastKey") val fastKey: String,
        @Json(name = "movieId") val movieId: Int,
        @Json(name = "isLike") val isLike: Int
)

@JsonClass(generateAdapter = true)
data class ApiMovieCollection(
        @Json(name = "uid") val uid: Long,
        @Json(name = "movieId") val movieId: Int,
        @Json(name = "isCollection") val isCollection: Int,
        @Json(name = "fastKey") val fastKey: String
)

@JsonClass(generateAdapter = true)
data class ApiId(
        @Json(name = "retCode") val code: Int,
        @Json(name = "id") val id: Int
)

data class ApiMovieHistory(
        @Json(name = "retCode") val code: Int,
        @Json(name = "movieList") val movies: List<ApiMovie.Movie>?,
) {
    @JsonClass(generateAdapter = true)
    data class Req(
            @Json(name = "fastKey") val fastKey: String,
            @Json(name = "uid") val uid: Long
    )
}

@JsonClass(generateAdapter = true)
data class ApiCollectionReq(
        @Json(name = "uid") val uid: Long,
        @Json(name = "page") val page: Int,
        @Json(name = "size") val size: Int,
        @Json(name = "fastkey") val fastKey: String
)

@JsonClass(generateAdapter = true)
data class ApiNumReq(
        @Json(name = "movieId") val movieId: Int,
        @Json(name = "deviceId") val deviceId: String,
        @Json(name = "uid") val uid: Long,
        @Json(name = "channelId") val channelId: Int
)

@JsonClass(generateAdapter = true)
data class ApiDurationRecordReq(
        @Json(name = "movieId") val movieId: Int,
        @Json(name = "deviceId") val deviceId: String,
        @Json(name = "uid") val uid: Long,
        @Json(name = "channelId") val channelId: Int,
        @Json(name = "duration") val duration: Long
)

@JsonClass(generateAdapter = true)
data class ApiMovieCollectionList(
        @Json(name = "movieList") val movieList: List<Movie>,
        @Json(name = "next") val next: Int,
        @Json(name = "retCode") val retCode: Int,
        @Json(name = "totalCount") val totalCount: Int,
        @Json(name = "totalPage") val totalPage: Int,
) {

    @JsonClass(generateAdapter = true)
    data class Movie(
            @Json(name = "byuid") val byuid: Int,
            @Json(name = "captions") val captions: Int,
            @Json(name = "category") val category: String,
            @Json(name = "comments") val comments: Int,
            @Json(name = "country") val country: Int,
            @Json(name = "cover") val cover: String,
            @Json(name = "desc") val desc: String,
            @Json(name = "download") val download: String,
            @Json(name = "downloadurl") val downloadurl: String,
            @Json(name = "duration") val duration: Int,
            @Json(name = "follow") val follow: Int,
            @Json(name = "id") val id: Int,
            @Json(name = "isFollowed") val isFollowed: Boolean,
            @Json(name = "isLiked") val isLiked: Boolean,
            @Json(name = "isportait") val isportait: Int,
            @Json(name = "like") val like: Int,
            @Json(name = "mosaic") val mosaic: Int,
            @Json(name = "movieId") val movieId: Int,
            @Json(name = "movieUrl") val movieUrl: String,
            @Json(name = "name") val name: String,
            @Json(name = "playnum") val playnum: Int,
            @Json(name = "propaganda") val propaganda: String,
            @Json(name = "recommend") val recommend: Int,
            @Json(name = "score") val score: Int,
            @Json(name = "share") val share: Int,
            @Json(name = "uploadtm") val uploadtm: Int,
            @Json(name = "year") val year: Int
    )
}
//-------⬆⬆⬆⬆⬆⬆⬆⬆----长视频相关----⬆⬆⬆⬆⬆⬆⬆-----//
