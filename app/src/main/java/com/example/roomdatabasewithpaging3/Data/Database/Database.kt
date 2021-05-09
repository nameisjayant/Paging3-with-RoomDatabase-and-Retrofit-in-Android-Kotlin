package com.example.roomdatabasewithpaging3.Data.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.roomdatabasewithpaging3.Data.Dao.Dao
import com.example.roomdatabasewithpaging3.Data.Dao.RemoteKeysDao
import com.example.roomdatabasewithpaging3.Data.Dogs
import com.example.roomdatabasewithpaging3.Data.RemoteKeys

@Database(entities = [Dogs::class,RemoteKeys::class],version = 1,exportSchema = false)
abstract class Database : RoomDatabase(){

    abstract fun getDao(): Dao
    abstract fun remoteKeyDao():RemoteKeysDao
}