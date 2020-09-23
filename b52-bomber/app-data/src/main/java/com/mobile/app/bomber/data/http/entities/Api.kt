package com.mobile.app.bomber.data.http.entities

import com.mobile.guava.https.nullSafe
import com.mobile.guava.jvm.math.MathUtils
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import java.io.Serializable

private const val HOST_TEST = "http://119.28.18.135:"
private const val HOST_UPLOAD_TEST = "http://119.28.54.13:"
private const val HOST_SearchAVersion_TEST = "http://192.168.2.120:"

const val HOST_USER = "${HOST_TEST}8000"
const val HOST_VIDEO = "${HOST_TEST}8001"
const val HOST_SearchAVersion = "${HOST_TEST}8001"

const val HOST_SYS = "${HOST_TEST}8003"
const val HOST_UPLOAD = "${HOST_UPLOAD_TEST}8080"
const val DECODE_URL = "${HOST_UPLOAD_TEST}8000"

const val API_USER = ""
const val API_VIDEO = ""
const val API_SYS = ""


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
            return desc.nullSafe() + label.nullSafe()
        }

        fun decodeVideoUrl(): String {
//            return "${DECODE_URL}/videodecode?videourl=${videoURL}&download=0"

            return videoURL

        }

        fun downloadVideoUrl(): String {
//            return "${DECODE_URL}/videodecode?videourl=${videoURL}"
            return null!!
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
        @Json(name = "duration") val duration: Long
)

@JsonClass(generateAdapter = true)
data class ApiMovie(
        @Json(name = "RetCode") val code: Int,
        @Json(name = "MovieList") val movies: List<Movie>?
) {

    @JsonClass(generateAdapter = true)
    data class Movie(
            @Json(name = "movieId") val movieId: Long
    )
}


@JsonClass(generateAdapter = true)
data class ApiSearch(
        @Json(name = "RetCode") val code: Int,
        @Json(name = "TodayLabels") val todayLabels: List<TodayLabels>?,
        @Json(name = "RecommendLabels") val recommendLabels: List<String>?,
) {

    @JsonClass(generateAdapter = true)
    data class TodayLabels(
            @Json(name = "title") val title: String,
            @Json(name = "rank") val rankDesc: String
    )
}

@JsonClass(generateAdapter = true)
data class ApiSearchVideo(
        @Json(name = "RetCode") val code: Int,
        @Json(name = "Desc") val desc: String,
        @Json(name = "totalcount") val totalCount: Int,
        @Json(name = "next") val nextPage: Int,
        @Json(name = "totalpage") val totalPage: Int,
        @Json(name = "VideoList") val Videos: List<ApiVideo.Video>?
)

@JsonClass(generateAdapter = true)
data class ApitHotKey(
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