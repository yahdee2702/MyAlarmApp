package com.idn.myalarmapp.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query

@Entity
data class Alarm (
    @PrimaryKey (autoGenerate = true)
    val id : Int,
    val time : String,
    val date : String,
    val note : String,
    val type : Int,
    val requestCode: Int
)