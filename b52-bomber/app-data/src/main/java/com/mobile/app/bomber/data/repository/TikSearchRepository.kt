package com.mobile.app.bomber.data.repository

import com.mobile.app.bomber.data.db.AppDatabase
import com.mobile.app.bomber.data.db.entities.DbTikSearchKey
import com.mobile.app.bomber.data.files.AppPrefsManager
import com.mobile.app.bomber.data.http.service.DataService
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TikSearchRepository @Inject constructor(
        dataService: DataService,
        db: AppDatabase,
        appPrefsManager: AppPrefsManager
) : BaseRepository(dataService, db, appPrefsManager) {

    fun getKeys(): Flow<List<DbTikSearchKey>> {
        val dao = db.tikSearchKeyDao()
        return dao.get()
    }

    fun addKey(key: String): DbTikSearchKey {
        val dao = db.tikSearchKeyDao()
        val dbSearchKey = dao.get(key)
        val obj: DbTikSearchKey
        if (dbSearchKey == null) {
            dao.insert(DbTikSearchKey(0, key, System.currentTimeMillis()).also {
                obj = it
            })
        } else {
            dao.update(dbSearchKey.copy(time = System.currentTimeMillis()).also {
                obj = it
            })
        }
        return obj
    }

    fun clearKeys(): Int {
        val dao = db.tikSearchKeyDao()
        return dao.clear()
    }

    fun deleteKey(obj: DbTikSearchKey): Int {
        val dao = db.tikSearchKeyDao()
        return dao.delete(obj)
    }
}