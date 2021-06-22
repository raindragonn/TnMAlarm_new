package com.bluepig.tnmalarm.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.bluepig.tnmalarm.model.Alarm
import com.bluepig.tnmalarm.ui.alarm.AlarmActivity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import java.util.*

class AlarmBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent != null) {
            val alarm = intent.getBundleExtra(AlarmActivity.EXTRA_ALARM)
                ?.getParcelable<Alarm>(AlarmActivity.EXTRA_ALARM)

            if (context != null && alarm != null && alarm.onOff) {
                if (isActive(alarm)) {
                    context.startActivity(
                        Intent(
                            context,
                            AlarmActivity::class.java
                        ).apply {
                            putExtra(
                                AlarmActivity.EXTRA_ALARM,
                                alarm
                            )
                            addFlags(
                                Intent.FLAG_ACTIVITY_NEW_TASK
                                        or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            )
                        })
                    if (alarm.weak.toConvertString() == "반복없음") {
                        cancel(context, alarm)
                        return
                    }
                }
                setAlarm(context, alarm)
            }
        }
    }

    fun isActive(alarm: Alarm): Boolean {
        if (alarm.weak.toConvertString() == "반복없음") {
            return true
        }

        return when (Calendar.getInstance().get(Calendar.DAY_OF_WEEK)) {
            1 -> {
                alarm.weak.getSunDay()
            }
            2 -> {
                alarm.weak.getMonday()
            }
            3 -> {
                alarm.weak.getTuesday()
            }
            4 -> {
                alarm.weak.getWednesday()
            }
            5 -> {
                alarm.weak.getThursday()
            }
            6 -> {
                alarm.weak.getFriday()
            }
            7 -> {
                alarm.weak.getSaturday()
            }
            else -> false
        }
    }

    private fun setAlarm(context: Context, alarm: Alarm) {
        try {
            if (alarm.date == null) {
                return
            }

            val mCalendar: Calendar = Calendar.getInstance().apply {
                time = Date()
                set(Calendar.HOUR_OF_DAY, alarm.date!!.hour)
                set(Calendar.MINUTE, alarm.date!!.minute)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val nowCalendar = Calendar.getInstance().apply {
                time = Date()
            }

            if (mCalendar.before(nowCalendar) || nowCalendar.time == mCalendar.time) { //이미 지난 시간 일 경우
                mCalendar.add(Calendar.DATE, 1)
            }

            val triggerTime = mCalendar.timeInMillis

            val pendingIntent =
                PendingIntent.getBroadcast(
                    context,
                    alarm.id!!,
                    Intent(context, AlarmBroadcastReceiver::class.java).apply {
                        putExtra(
                            AlarmActivity.EXTRA_ALARM,
                            Bundle().apply { putParcelable(AlarmActivity.EXTRA_ALARM, alarm) })
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

            val alarmManager = (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)
            val info: AlarmManager.AlarmClockInfo =
                AlarmManager.AlarmClockInfo(triggerTime, null)
            alarmManager.setAlarmClock(info, pendingIntent)

            //절전모드에서도 동작하기
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).setExactAndAllowWhileIdle(
//                    AlarmManager.RTC_WAKEUP,
//                    triggerTime,
//                    pendingIntent
//                )
//            } else {
//                (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).setExact(
//                    AlarmManager.RTC_WAKEUP,
//                    triggerTime,
//                    pendingIntent
//                )
//            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun cancel(context: Context, alarm: Alarm) {
        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                alarm.id!!,
                Intent(context, AlarmBroadcastReceiver::class.java).apply {
                    putExtra(AlarmActivity.EXTRA_ALARM, alarm)
                },
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager).cancel(pendingIntent)
    }
}