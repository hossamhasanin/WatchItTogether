package com.hossam.hasanin.base

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hossam.hasanin.base.dataScources.UserCashDataSource
import com.hossam.hasanin.base.models.ArrayListConverter
import com.hossam.hasanin.base.models.User
import com.hossam.hasanin.base.models.WatchRoom

@Database(entities = [User::class , WatchRoom::class] , version = 1)
@TypeConverters(ArrayListConverter::class)
abstract class MainDataBase: RoomDatabase() {
    abstract fun cashDao() : UserCashDataSource
}