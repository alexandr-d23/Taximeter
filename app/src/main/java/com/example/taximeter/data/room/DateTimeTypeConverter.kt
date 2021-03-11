package com.example.taximeter.data.room

import androidx.room.TypeConverter
import org.joda.time.DateTime


class DateTimeTypeConverter {

    @TypeConverter
    fun toDateTime(timestamp: Long) : DateTime{
        return DateTime(timestamp)
    }

    @TypeConverter
    fun fromDateTime(dateTime: DateTime): Long {
        return dateTime.millis
    }
}