package com.mobile.app.bomber.data.db

import androidx.room.Dao
import androidx.room.Query
import com.mobile.app.bomber.data.db.entities.DbUser
import com.mobile.guava.data.SqlDao

@Dao
interface UserDao : SqlDao<DbUser> {

    @Query("SELECT * FROM user WHERE _id =:id")
    fun get(id: Int): DbUser?
}