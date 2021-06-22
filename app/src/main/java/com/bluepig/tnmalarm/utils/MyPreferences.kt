package com.bluepig.tnmalarm.utils

import android.content.Context
import com.bluepig.tnmalarm.utils.ext.readSharedPreference
import com.bluepig.tnmalarm.utils.ext.writeSharedPreference

object MyPreferences {
    const val PREFERENCES_NAME = "myPre"

    const val DEFAULT_VALUE_INT = 0
    const val DEFAULT_VALUE_LONG = 10L * 1000
    const val DEFAULT_VALUE_STRING = ""
    const val DEFAULT_VALUE_BOOLEAN = false

    private const val KEY_SHOW_TUTORIAL = "showTutorial"
    private const val KEY_DEFAULT_RINGTONE_URI = "defaultRingtoneUri"
    private const val KEY_USE_ADMOB = "useAdmob"
    private const val KEY_INTERSTITIAL_INTERVAL = "interstitialInterval"
    private const val KEY_INTERSTITIAL_LAST_TIME = "interstitialLastTime"

    fun writeShowTutorial(context: Context, setting: Boolean = DEFAULT_VALUE_BOOLEAN) {
        context.writeSharedPreference(KEY_SHOW_TUTORIAL, setting)
    }

    fun readShowTutorial(context: Context): Boolean = context.readSharedPreference(
        KEY_SHOW_TUTORIAL,
        DEFAULT_VALUE_BOOLEAN
    ) ?: DEFAULT_VALUE_BOOLEAN

    fun writeDefaultRingTone(context: Context, ringtoneUri: String = DEFAULT_VALUE_STRING) {
        context.writeSharedPreference(KEY_DEFAULT_RINGTONE_URI, ringtoneUri)
    }

    fun readDefaultRingTone(context: Context): String = context.readSharedPreference(
        KEY_DEFAULT_RINGTONE_URI, DEFAULT_VALUE_STRING
    ) ?: DEFAULT_VALUE_STRING

    fun writeUseAdmob(context: Context,setting: Boolean = DEFAULT_VALUE_BOOLEAN){
        context.writeSharedPreference(KEY_USE_ADMOB,setting)
    }

    fun readUseAdmob(context: Context): Boolean = context.readSharedPreference(KEY_USE_ADMOB,
        DEFAULT_VALUE_BOOLEAN) ?: DEFAULT_VALUE_BOOLEAN

    fun writeInterstitialInterval(context: Context,setting: Long = DEFAULT_VALUE_LONG){
        context.writeSharedPreference(KEY_INTERSTITIAL_INTERVAL,setting)
    }

    fun readInterstitialInterval(context: Context): Long = context.readSharedPreference(
        KEY_INTERSTITIAL_INTERVAL,
        DEFAULT_VALUE_LONG) ?: DEFAULT_VALUE_LONG


    fun writeInterstitialLastTime(context: Context,setting: Long = DEFAULT_VALUE_LONG){
        context.writeSharedPreference(KEY_INTERSTITIAL_LAST_TIME,setting)
    }

    fun readInterstitialLastTime(context: Context): Long = context.readSharedPreference(
        KEY_INTERSTITIAL_LAST_TIME,
        DEFAULT_VALUE_LONG) ?: DEFAULT_VALUE_LONG

}