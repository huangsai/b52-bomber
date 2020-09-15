package com.mobile.app.bomber.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
        tableName = "tik_search_key"
)
data class DbTikSearchKey(
        @ColumnInfo(name = "_id") @PrimaryKey(autoGenerate = true) val _id: Long,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "time") val time: Long
)