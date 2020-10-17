package com.mobile.app.bomber.data.db

interface AppDatabase {

    fun userDao(): UserDao

    fun tikSearchKeyDao(): TikSearchKeyDao

    fun movieSearchKeyDao(): MovieSearchKeyDao

    fun tikMessageDao(): TikMessageDao
}