package com.mobile.app.bomber.data.repository

import com.mobile.app.bomber.data.db.AppDatabase
import com.mobile.app.bomber.data.db.entities.DbTikMessageKey
import com.mobile.app.bomber.data.files.AppPrefsManager
import com.mobile.app.bomber.data.http.entities.*
import com.mobile.app.bomber.data.http.service.DataService
import com.mobile.guava.data.toSource
import com.mobile.guava.jvm.domain.Source
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MsgRepository @Inject constructor(
        dataService: DataService,
        db: AppDatabase,
        appPrefsManager: AppPrefsManager
) : BaseRepository(dataService, db, appPrefsManager) {

    suspend fun postUserMsg(msgtype: Int, timestemp: Int): Source<List<ApiUsermsg.Item>> {
        if (!appPrefsManager.isLogin()) {
            return Source.Error(sourceException403)
        }
        val req = ApipostUserMsg(
                msgtype,
                timestemp,
                userId,
                token,
        )
        return try {
            dataService.postUserMsg(req).toSource() {
                it.items.orEmpty()
            }
        } catch (e: Exception) {
            errorSource(e)
        }
    }


//    fun getKeys(): Flow<List<DbTikMessageKey>> {
//        val dao = db.tikMessageDao()
//        return dao.get()
//    }

    fun getKey(): DbTikMessageKey? {
        val dao = db.tikMessageDao()
        return dao.get(userId)
    }

    fun addKey(data: String): DbTikMessageKey {
        val dao = db.tikMessageDao()
        val db = dao.get(userId)
        val obj: DbTikMessageKey
        if (db == null) {
            dao.insert(DbTikMessageKey(0, userId, data).also {
                obj = it
            })
        } else {
            dao.update(db.copy(obj = data).also {
                obj = it
            })
        }
        return obj
    }

    fun clearKeys(): Int {
        val dao = db.tikMessageDao()
        return dao.clear()
    }

    fun deleteKey(obj: DbTikMessageKey): Int {
        val dao = db.tikMessageDao()
        return dao.delete(obj)
    }
}





