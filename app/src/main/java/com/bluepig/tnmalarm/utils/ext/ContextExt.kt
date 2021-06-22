package com.bluepig.tnmalarm.utils.ext

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.core.content.edit
import com.bluepig.tnmalarm.utils.MyPreferences

fun Context.checkNetwork(): Boolean {
    return try {
        val connectivityManager =
            getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork: NetworkInfo? = connectivityManager.activeNetworkInfo
        activeNetwork?.isConnectedOrConnecting == true
    } catch (e: Exception) {
        false
    }
}
fun internetCheck(c: Context): Boolean {
    val cmg = c.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        // Android 10+
        cmg.getNetworkCapabilities(cmg.activeNetwork)?.let { networkCapabilities ->
            return networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
                    || networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
        }
    } else {
        return cmg.activeNetworkInfo?.isConnectedOrConnecting == true
    }

    return false
}

fun Context.mToast(msg: String) {
    Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
}

fun Context.mToast(@StringRes msg: Int) {
    Toast.makeText(this, getString(msg), Toast.LENGTH_SHORT).show()
}

inline fun <reified T> Context.readSharedPreference(key: String, defaultValue: T): T? {
    var result: T?

    try {
        result = when (defaultValue) {
            is Int -> {
                this.getSharedPreferences(MyPreferences.PREFERENCES_NAME, Activity.MODE_PRIVATE)
                    .getInt(key, defaultValue) as T
            }

            is Long -> {
                this.getSharedPreferences(MyPreferences.PREFERENCES_NAME, Activity.MODE_PRIVATE)
                    .getLong(key, defaultValue) as T
            }

            is Boolean -> {
                this.getSharedPreferences(MyPreferences.PREFERENCES_NAME, Activity.MODE_PRIVATE)
                    .getBoolean(key, defaultValue) as T
            }

            is String -> {
                this.getSharedPreferences(MyPreferences.PREFERENCES_NAME, Activity.MODE_PRIVATE)
                    .getString(key, defaultValue) as T
            }
            else -> {
                null
            }
        }
    } catch (e: Exception) {
        result = when (defaultValue) {
            is Int, Long, Boolean, String -> {
                defaultValue
            }
            else -> null
        }
    }

    return result
}

fun <T> Context.writeSharedPreference(key: String, value: T) {
    this.getSharedPreferences(MyPreferences.PREFERENCES_NAME, Activity.MODE_PRIVATE).edit(true) {
        when (value) {
            is Int -> {
                putInt(key, value)
            }
            is Long -> {
                putLong(key, value)
            }
            is Boolean -> {
                putBoolean(key, value)
            }
            is String -> {
                putString(key, value)
            }
        }
    }
}
