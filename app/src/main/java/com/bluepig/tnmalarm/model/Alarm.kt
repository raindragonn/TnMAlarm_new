package com.bluepig.tnmalarm.model

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import kotlinx.parcelize.RawValue

import org.threeten.bp.LocalTime


@Entity
@Parcelize
data class Alarm(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,

    var title: String?,

    var songTitle: String?,

    var date: LocalTime?,

    var weak: @RawValue Weak,

    var url: String?,

    var type: Int?,

    var volume: Int?,

    var onOff: Boolean = true,

    var vibrate: Boolean = true,

    var songUrl : String?
) : Parcelable {
    override fun toString(): String {
        return "id = $id \n title = $title \n songTitle = $songTitle \n  date = $date \n weak = ${weak.toConvertString()} \n url = $url \n type = $type \n volume = $volume \n onoff = $onOff \n vibrate = $vibrate"
    }
}



