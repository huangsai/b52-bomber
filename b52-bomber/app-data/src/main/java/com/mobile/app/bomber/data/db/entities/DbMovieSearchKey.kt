package com.mobile.app.bomber.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
        tableName = "movie_search_key"
)
data class DbMovieSearchKey(
        @ColumnInfo(name = "_id") @PrimaryKey(autoGenerate = true) val _id: Long,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "time") val time: Long
)