package com.bluepig.tnmalarm.utils

import android.util.Base64
import com.bluepig.tnmalarm.lib.AES256Cipher


object MyEncoder {
    init {
        System.loadLibrary("mk")
    }

    private external fun tnmAlarm(): String
    private external fun bluePig(): String

    private val base2: ByteArray by lazy { Base64.decode(bluePig(), Base64.NO_WRAP) }

    fun getBluePig(): String = String(base2)

    fun encodeText(src: String): String {
        if (src.isEmpty())
            return ""

        val out: String
        try {
            val base = Base64.decode(tnmAlarm(), Base64.NO_WRAP)
            out = AES256Cipher.AES_Encode(src, String(base))
        } catch (e: Exception) {
            return e.message!!
        }
        return out
    }

    fun decodeText(src: String): String {
        if (src.isEmpty())
            return ""

        val out: String
        try {
            val base = Base64.decode(tnmAlarm(), Base64.NO_WRAP)
            out = AES256Cipher.AES_Decode(src, String(base))
        } catch (e: Exception) {
            return ""
        }

        return out
    }
}
