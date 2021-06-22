package com.bluepig.tnmalarm.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Weak(
    private var monday: Boolean = false,
    private var tuesday: Boolean = false,
    private var wednesday: Boolean = false,
    private var thursday: Boolean = false,
    private var friday: Boolean = false,
    private var saturday: Boolean = false,
    private var sunday: Boolean = false
) : Parcelable {
    companion object {
        fun getDaily(): Weak =
            Weak(
                monday = true,
                tuesday = true,
                wednesday = true,
                thursday = true,
                friday = true,
                saturday = true,
                sunday = true
            )

        fun getNone(): Weak =
            Weak(
                monday = false,
                tuesday = false,
                wednesday = false,
                thursday = false,
                friday = false,
                saturday = false,
                sunday = false
            )

        fun toWeak(strWeak: String): Weak {
            when (strWeak) {
                "매일" -> {
                    return getDaily()
                }
                "반복없음" -> {
                    return getNone()
                }
                else -> {
                    val list = strWeak.split(",").map { it.trim() }
                    var result = Weak()
                    list.forEach {
                        if (it == "월") {
                            result = result.copy(monday = true)
                        }
                        if (it == "화") {
                            result = result.copy(tuesday = true)
                        }
                        if (it == "수") {
                            result = result.copy(wednesday = true)
                        }
                        if (it == "목") {
                            result = result.copy(thursday = true)
                        }
                        if (it == "금") {
                            result = result.copy(friday = true)
                        }
                        if (it == "토") {
                            result = result.copy(saturday = true)
                        }
                        if (it == "일") {
                            result = result.copy(sunday = true)
                        }
                    }
                    return result
                }
            }
        }
    }

    fun getMonday(): Boolean = monday
    fun getTuesday(): Boolean = tuesday
    fun getWednesday(): Boolean = wednesday
    fun getThursday(): Boolean = thursday
    fun getFriday(): Boolean = friday
    fun getSaturday(): Boolean = saturday
    fun getSunDay(): Boolean = sunday

    fun onMondayClick() {
        this.monday = !this.monday
    }

    fun onTuesdayClick() {
        this.tuesday = !this.tuesday
    }

    fun onWednesdayClick() {
        this.wednesday = !this.wednesday
    }

    fun onThursdayClick() {
        this.thursday = !this.thursday
    }

    fun onFridayClick() {
        this.friday = !this.friday
    }

    fun onSaturdayClick() {
        this.saturday = !this.saturday
    }

    fun onSunDayClick() {
        this.sunday = !this.sunday
    }


    fun toConvertString(): String {
        return when (this) {
            getDaily() -> "매일"
            getNone() -> "반복없음"
            else -> {
                val arr = arrayListOf<String>()
                if (this.getMonday()) {
                    arr.add("월")
                }
                if (this.getTuesday()) {
                    arr.add("화")
                }
                if (this.getWednesday()) {
                    arr.add("수")
                }
                if (this.getThursday()) {
                    arr.add("목")
                }
                if (this.getFriday()) {
                    arr.add("금")
                }
                if (this.getSaturday()) {
                    arr.add("토")
                }
                if (this.getSunDay()) {
                    arr.add("일")
                }
                arr.joinToString()
            }
        }
    }
}