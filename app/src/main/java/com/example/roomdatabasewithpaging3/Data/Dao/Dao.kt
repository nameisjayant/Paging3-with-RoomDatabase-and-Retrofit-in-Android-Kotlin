package com.example.roomdatabasewithpaging3.Data.Dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.roomdatabasewithpaging3.Data.Dogs

@Dao
interface Dao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(list: List<Dogs>)

    @Query("SELECT * FROM dogs")
    fun getAllDogs():PagingSource<Int, Dogs>

    @Query("DELETE FROM dogs")
    fun clearAll()
}