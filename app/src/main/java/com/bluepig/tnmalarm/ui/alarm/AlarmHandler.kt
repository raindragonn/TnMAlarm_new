package com.bluepig.tnmalarm.ui.alarm

import android.os.Handler
import android.os.Looper
import android.os.Message
import java.text.SimpleDateFormat
import java.util.*


class AlarmHandler(looper: Looper) : Handler(looper) {
    companion object {
        private const val MSG_START = 1
        private const val MSG_END = 2
    }

    private var repeat: Boolean = false
    private var onTimerCallback: ((String) -> Unit)? = null


    fun setTimerCallback(callback: (String) -> Unit) {
        onTimerCallback = callback
    }

    override fun handleMessage(msg: Message) {

        when (msg.what) {
            MSG_START -> {
                onTimerRepeat()
            }
            MSG_END -> {
                onTimerStop()
            }
        }
    }

    fun messageStart() {
        sendEmptyMessage(MSG_START)
        repeat = true
    }

    fun messageEnd() {
        sendEmptyMessage(MSG_END)
        repeat = false
    }

    private fun onTimerStop() {
        removeMessages(MSG_START)
    }

    private fun onTimerRepeat() {
        if (repeat) {

            onTimerCallback?.invoke(SimpleDateFormat("HH:mm", Locale.getDefault()).run {
                format(Date(System.currentTimeMillis()))
            })
            sendEmptyMessageDelayed(MSG_START, 1000)
        }
    }
}