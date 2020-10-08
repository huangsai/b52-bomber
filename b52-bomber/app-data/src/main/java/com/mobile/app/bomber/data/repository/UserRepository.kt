package com.mobile.app.bomber.data.repository

import com.mobile.app.bomber.data.db.AppDatabase
import com.mobile.app.bomber.data.files.AppPrefsManager
import com.mobile.app.bomber.data.http.entities.*
import com.mobile.guava.data.nullSafe
import com.mobile.guava.data.toSource
import com.mobile.app.bomber.data.http.service.DataService
import com.mobile.guava.data.bodyOrThrowException
import com.mobile.guava.jvm.domain.Source
import kotlinx.coroutines.async
import kotlinx.coroutines.supervisorScope
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
        dataService: DataService,
        db: AppDatabase,
        appPrefsManager: AppPrefsManager
) : BaseRepository(dataService, db, appPrefsManager) {

    private val _aboutUsers = ArrayList<ApiAtUser.User>()
    val aboutUsers: List<ApiAtUser.User> get() = _aboutUsers

    private suspend fun aboutUsers(): Source<List<ApiAtUser.User>> = supervisorScope {
        return@supervisorScope try {
            _aboutUsers.clear()
            val deferredFan = async {
                dataService.fanList(userId, token).execute().bodyOrThrowException()
            }
            val deferredFollow = async {
                dataService.followList(userId, token).execute().bodyOrThrowException()
            }
            val fan = deferredFan.await()
            val follow = deferredFollow.await()
            _aboutUsers.addAll((fan.follows.orEmpty().plus(follow.follows.orEmpty())).map { it.toUser() })
            Source.Success(_aboutUsers)
        } catch (e: Exception) {
            errorSource<List<ApiAtUser.User>>(e)
        }
    }

    suspend fun searchUsers(keyword: String): Source<List<ApiAtUser.User>> {
        return try {
            dataService.searchUsers(keyword).toSource {
                it.users.orEmpty()
            }
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    // 正常登录 登录类型 1游客 2手机号
    suspend fun login(telephone: String, verifyCode: String): Source<ApiToken> {
        val loginType = if (telephone.isEmpty()) 1 else 2
        val call = dataService.login(ApiToken.AReq(
                1,
                loginType,
                appPrefsManager.getDeviceId(),
                telephone,
                verifyCode
        ))
        return try {
            call.execute().toSource().apply {
                if (this is Source.Success && data.code == 0) {
                    if (loginType == 2) {
                        appPrefsManager.setUserId(data.uid.nullSafe())
                        appPrefsManager.setToken(data.token.nullSafe())
                        appPrefsManager.setIsLogin(true)
                        aboutUsers()
                    } else if (loginType == 1) {
                        appPrefsManager.setUserId(data.uid.nullSafe())
                        appPrefsManager.setToken(data.token.nullSafe())
                        appPrefsManager.setIsLogin(false)
                    }
                }
            }
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    //快捷登录 登录类型 1游客 2手机号
    suspend fun fastLogin(): Source<ApiToken> {
        val call = dataService.fastLogin(ApiToken.Req(
                userId, token
        ))
        return try {
            call.execute().toSource().apply {
                if (this is Source.Success && data.code == 0) {
                    appPrefsManager.setUserId(data.uid.nullSafe())
                    appPrefsManager.setToken(data.token.nullSafe())
                    appPrefsManager.setIsLogin(true)
                    aboutUsers()
                }
            }
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    /**
     * NOTE: notFollowing
     */
    suspend fun follow(targetUserId: Long, notFollowing: Boolean): Source<ApiFollow> {
        val call = dataService.follow(ApiFollow.Req(
                userId, token, targetUserId, notFollowing
        ))
        return try {
            call.execute().toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun getVerifyCode(telephone: String): Source<Nope> {
        val call = dataService.verifyCode(telephone);
        return try {
            call.execute().toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun getUserInfo(uid: Long): Source<ApiUser> {
        val call = dataService.user(uid)
        return try {
            call.execute().toSource().apply {
                if (this is Source.Success && data.code == 0) {
                    saveUserInfo(requireData())
                }
            }
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun updateBirthday(birthday: String): Source<Nope> {
        val call = dataService.updateBirthday(ApiUser.BirthdayReq(
                userId, token, birthday
        ))
        return try {
            call.execute().toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun updateGender(gender: Int): Source<Nope> {
        val call = dataService.updateGender(ApiUser.GenderReq(
                userId, token, gender
        ))
        return try {
            call.execute().toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun updateSign(sign: String): Source<Nope> {
        val call = dataService.updateSign(ApiUser.SignReq(
                userId, token, sign
        ))
        return try {
            call.execute().toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun updateNickname(nickName: String): Source<Nope> {
        val call = dataService.updateNickname(ApiUser.NicknameReq(
                userId, token, nickName
        ))
        return try {
            call.execute().toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun updateWechatID(wechatID: String): Source<Nope> {
        val call = dataService.updateWechat(ApiUser.WechatReq(
                userId, token, wechatID
        ))
        return try {
            call.execute().toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun isFollowing(targetUserId: Long): Source<ApiIsFollowing> {
        val call = dataService.isFollowing(
                userId, token, targetUserId
        )
        return try {
            call.execute().toSource()
        } catch (e: Exception) {

            errorSource(e)
        }
    }

    suspend fun followList(): Source<List<ApiFollow.Follow>> {
        val call = dataService.followList(
                userId, token
        )
        return try {
            call.execute().toSource { data ->
                data.follows.orEmpty()
            }
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun fanList(): Source<List<ApiFollow.Follow>> {
        val call = dataService.fanList(userId, token)
        return try {
            call.execute().toSource { data ->
                data.follows.orEmpty()
            }
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    suspend fun ranksOfUser(type: String, time: Long, pager: Pager): Source<List<ApiRank.Rank>> {
        pager.isBusy = true
        val call = dataService.ranksOfUser(
                userId, token, type, time, pager.requestPage, pager.pageSize
        )
        return try {
            call.execute().toSource {
                pager.nextPage(it.totalPage)
                it.ranks.orEmpty()
            }
        } catch (e: Exception) {
            pager.isBusy = false
            errorSource(e)
        }
    }

    suspend fun getUserCount(uid: Long): Source<ApiUserCount> {
        val call = dataService.getUserCount(uid)
        return try {
            call.execute().toSource()
        } catch (e: Exception) {
            errorSource(e)
        }
    }

    private fun saveUserInfo(userInfo: ApiUser) {
        appPrefsManager.setLoginName(userInfo.username.nullSafe())
        appPrefsManager.setHeadPicUrl(userInfo.Pic.nullSafe())
    }

    fun logout() {
        appPrefsManager.setIsLogin(false)
        _aboutUsers.clear()
    }
}