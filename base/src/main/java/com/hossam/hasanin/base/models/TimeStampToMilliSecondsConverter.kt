package com.hossam.hasanin.base.models

import androidx.room.TypeConverter
import com.google.firebase.Timestamp
import java.util.*
import kotlin.collections.ArrayList

class TimeStampToMilliSecondsConverter {
    @TypeConverter
    fun fromTimeStampToDate(timestamp: Timestamp) : Long{
        return timestamp.toDate().time
    }
    @TypeConverter
    fun fromMilliSecondsToTimeStamp(time: Long): Timestamp{
        return Timestamp(Date(time))
    }
}