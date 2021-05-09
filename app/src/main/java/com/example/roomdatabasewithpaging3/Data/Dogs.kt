package com.example.roomdatabasewithpaging3.Data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json

@Entity(tableName = "dogs")
data class Dogs(
    @PrimaryKey
    val id:String,
    @Json(name = "url")
    val url:String
){
}
