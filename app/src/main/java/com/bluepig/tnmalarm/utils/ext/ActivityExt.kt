package com.bluepig.tnmalarm.utils.ext

import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager

fun Activity.hideKeyboard() {
    val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?

    if (inputMethodManager != null && currentFocus != null) {
        inputMethodManager.hideSoftInputFromWindow(
            currentFocus!!.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }
}
fun Activity.setTurnScreenOnLock() {
    val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

    when {
        android.os.Build.VERSION.SDK_INT >= 27 -> {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
            keyguardManager.requestDismissKeyguard(this, null)
        }
        android.os.Build.VERSION.SDK_INT == 26 -> {
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
            keyguardManager.requestDismissKeyguard(this, null)
        }
        else -> {
            window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED)
            window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON)
            window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD)
        }
    }
    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
}
