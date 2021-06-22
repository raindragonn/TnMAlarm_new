package com.bluepig.tnmalarm.utils.ext

import android.util.Log
import com.bluepig.tnmalarm.Config

const val LOG_TAG = "bluePig_log"

fun logV(msg: String, tag: String = LOG_TAG, showCallStack: Boolean = false) {
    if (Config.DEBUG) {
        if (showCallStack) {
            Log.v(tag, msg, Throwable())
        } else {
            Log.v(tag, msg)
        }
    }
}


fun logD(msg: String, tag: String = LOG_TAG, showCallStack: Boolean = false) {
    if (Config.DEBUG) {
        if (showCallStack) {
            Log.d(tag, msg, Throwable())
        } else {
            Log.d(tag, msg)
        }
    }
}

fun logI(msg: String, tag: String = LOG_TAG, showCallStack: Boolean = false) {
    if (Config.DEBUG) {
        if (showCallStack) {
            Log.i(tag, msg, Throwable())
        } else {
            Log.i(tag, msg)
        }
    }
}

fun logW(msg: String, tag: String = LOG_TAG, showCallStack: Boolean = false) {
    if (Config.DEBUG) {
        if (showCallStack) {
            Log.w(tag, msg, Throwable())
        } else {
            Log.w(tag, msg)
        }
    }
}

fun logE(msg: String, tag: String = LOG_TAG, showCallStack: Boolean = false) {
    if (Config.DEBUG) {
        if (showCallStack) {
            Log.e(tag, msg, Throwable())
        } else {
            Log.e(tag, msg)
        }
    }
}