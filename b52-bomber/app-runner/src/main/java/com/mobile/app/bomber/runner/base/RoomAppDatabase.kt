package com.mobile.app.bomber.runner.base

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mobile.app.bomber.data.db.AppDatabase
import com.mobile.app.bomber.data.db.entities.DbMovieSearchKey
import com.mobile.app.bomber.data.db.entities.DbTikMessageKey
import com.mobile.app.bomber.data.db.entities.DbTikSearchKey
import com.mobile.app.bomber.data.db.entities.DbUser

@Database(
        entities = [
            DbUser::class,
            DbTikSearchKey::class,
            DbMovieSearchKey::class,
            DbTikMessageKey::class
        ],
        version = 1,
        exportSchema = true
)
abstract class RoomAppDatabase : RoomDatabase(), AppDatabase {

    class DbCallback : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
        }

        override fun onOpen(db: SupportSQLiteDatabase) {
            super.onOpen(db)
        }

        override fun onDestructiveMigration(db: SupportSQLiteDatabase) {
            super.onDestructiveMigration(db)
        }
    }
}