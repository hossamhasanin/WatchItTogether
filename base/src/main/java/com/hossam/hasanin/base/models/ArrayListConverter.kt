package com.hossam.hasanin.base.models

import androidx.room.TypeConverter

class ArrayListConverter {
    @TypeConverter
    fun fromArrayListToString(arr: ArrayList<String>) : String{
        return arr.reduce { acc, s -> "$acc,$s" }
    }
    @TypeConverter
    fun fromStringToList(s: String): ArrayList<String>{
        val l = arrayListOf<String>()
        l.addAll(s.split(","))
        return l
    }
}