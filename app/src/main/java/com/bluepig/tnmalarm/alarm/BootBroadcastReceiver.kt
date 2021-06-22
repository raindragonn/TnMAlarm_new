package com.bluepig.tnmalarm.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.bluepig.tnmalarm.TnMApplication
import com.bluepig.tnmalarm.model.Alarm
import com.bluepig.tnmalarm.room.AppDataBase
import com.bluepig.tnmalarm.ui.alarm.AlarmActivity
import com.bluepig.tnmalarm.utils.MyPreferences
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.logEvent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class BootBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            val intent1 = intent?.action.equals(Intent.ACTION_BOOT_COMPLETED)
            val intent2 =
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                    intent?.action.equals(Intent.ACTION_LOCKED_BOOT_COMPLETED)
                } else {
                    intent?.action.equals(Intent.ACTION_BOOT_COMPLETED)
                }

            if (intent1 || intent2) {

                context?.let { ctx ->
                    val dao = AppDataBase.getDatabase(ctx).alarmDao()
                    GlobalScope.launch {
                        withContext(Dispatchers.IO) {
                            dao.getAllList()
                        }.let {
                            for (alarm in it) {
                                cancel(ctx,alarm)
                                if (alarm.onOff) {
                                    setAlarm(ctx,alarm)
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
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

            val info: AlarmManager.AlarmClockInfo = AlarmManager.AlarmClockInfo(triggerTime, null)
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.setAlarmClock(info, pendingIntent)

        } catch (e: java.lang.Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    private fun cancel(context: Context, alarm: Alarm) {
        val pendingIntent =
            PendingIntent.getBroadcast(
                context,
                alarm.id!!,
                Intent(context, AlarmBroadcastReceiver::class.java).apply {
                    putExtra(AlarmActivity.EXTRA_ALARM, alarm)
                },
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        val alarmManager = (context.getSystemService(Context.ALARM_SERVICE) as AlarmManager)

        alarmManager.cancel(pendingIntent)
    }
}