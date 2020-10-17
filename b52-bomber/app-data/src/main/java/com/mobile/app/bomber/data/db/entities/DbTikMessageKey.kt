package com.mobile.app.bomber.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@Entity(
        tableName = "tik_message_key"
)
data class DbTikMessageKey(
        @ColumnInfo(name = "_id") @PrimaryKey(autoGenerate = true) val _id: Long,
        @ColumnInfo(name = "uid") val uid: Long,
        @ColumnInfo(name = "obj") val obj: String
)