package com.mobile.app.bomber.data.db

import androidx.room.Dao
import androidx.room.Query
import com.mobile.app.bomber.data.db.entities.DbTikMessageKey
import com.mobile.app.bomber.data.db.entities.DbTikSearchKey
import com.mobile.guava.data.SqlDao
import kotlinx.coroutines.flow.Flow

@Dao
interface TikMessageDao : SqlDao<DbTikMessageKey> {

    @Query("SELECT * FROM tik_message_key WHERE uid =:uid")
    fun get(uid: Long): DbTikMessageKey?

    @Query("DELETE FROM tik_search_key")
    fun clear(): Int
}