package com.bluepig.tnmalarm.room

import androidx.room.TypeConverter
import com.bluepig.tnmalarm.model.Weak
import org.threeten.bp.LocalTime

class AlarmConverter {
    @TypeConverter
    fun toLocalTIme(strTime: String): LocalTime = LocalTime.parse(strTime)

    @TypeConverter
    fun toString(localTime: LocalTime): String = localTime.toString()

    @TypeConverter
    fun toString(weak: Weak): String = weak.toConvertString()

    @TypeConverter
    fun toWeak(strWeak: String): Weak = Weak.toWeak(strWeak)
}