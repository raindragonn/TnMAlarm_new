package com.bluepig.tnmalarm.model

enum class AlarmType {
    YOUTUBE {
        override fun toInt(): Int = 1
    },
    SONG {
        override fun toInt(): Int = 2
    };
    abstract fun toInt(): Int
}