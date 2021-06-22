package com.bluepig.tnmalarm.ui.base

import android.Manifest
import android.app.AlarmManager
import android.app.KeyguardManager
import android.app.PendingIntent
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.AudioManager
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings
import android.view.KeyEvent
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.Observer
import com.bluepig.tnmalarm.R
import com.bluepig.tnmalarm.TnMApplication
import com.bluepig.tnmalarm.alarm.AlarmBroadcastReceiver
import com.bluepig.tnmalarm.model.Alarm
import com.bluepig.tnmalarm.room.AlarmDAO
import com.bluepig.tnmalarm.ui.alarm.AlarmActivity
import com.bluepig.tnmalarm.utils.ConnectivityLiveData
import com.bluepig.tnmalarm.utils.MyPreferences
import com.bluepig.tnmalarm.utils.ext.checkNetwork
import com.bluepig.tnmalarm.utils.ext.logD
import com.bluepig.tnmalarm.utils.ext.mToast
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import java.util.*

abstract class BaseActivity<B : ViewDataBinding>(@LayoutRes val layoutId: Int) :
    AppCompatActivity() {

    protected lateinit var binding: B

    abstract fun onCreate()
    abstract fun onNetworkChanged(connect: Boolean)

    private val _connect by lazy { ConnectivityLiveData(connectivityManager) }

    protected fun isNetworkConnected(): Boolean = _connect.value ?: run {
        checkNetwork()
    }

    private val connectivityManager: ConnectivityManager by lazy {
        applicationContext.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
    }
    private val vibrateManager: Vibrator by lazy {
        applicationContext.getSystemService(
            VIBRATOR_SERVICE
        ) as Vibrator
    }
    protected val audioManager: AudioManager by lazy {
        applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }
    protected val keyguardManager: KeyguardManager by lazy {
        applicationContext.getSystemService(
            Context.KEYGUARD_SERVICE
        ) as KeyguardManager
    }
    var vibrate: Boolean = false
    var defaultMusicVolume: Int? = null
    var defaultAlarmVolume: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, layoutId)

        if (::binding.isInitialized) {
            binding.lifecycleOwner = this

            _connect.observe(this, Observer {
                onNetworkChanged(it)
            })

            onCreate()
        }
    }

    fun getDao(): AlarmDAO = (application as TnMApplication).getDao()
    fun getAlarmManager(): AlarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

    fun <T> openAct(actClass: Class<T>, isFinish: Boolean = true) {
        startActivity(Intent(this, actClass))
        openPopupAnimation()
        if (isFinish) {
            mfinish()
        }
    }

    fun mfinish() {
        finish()
        closePopupAnimation()
    }

    fun alertDialog(
        title: String,
        message: String,
        positiveClick: () -> Unit,
        cancelClick: () -> Unit
    ) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.result, DialogInterface.OnClickListener { _, _ ->
                positiveClick()
            })
            .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { _, _ ->
                cancelClick()
            })
            .create()
            .show()
    }

    fun alertDialog(
        @StringRes
        title: Int,
        @StringRes
        message: Int,
        positiveClick: () -> Unit,
        cancelClick: () -> Unit
    ) {
        AlertDialog.Builder(this)
            .setTitle(getString(title))
            .setMessage(getString(message))
            .setPositiveButton(R.string.result, DialogInterface.OnClickListener { _, _ ->
                positiveClick()
            })
            .setNegativeButton(R.string.cancel, DialogInterface.OnClickListener { _, _ ->
                cancelClick()
            })
            .create()
            .show()
    }

    fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {   // 마시멜로우 이상일 경우
            if (!Settings.canDrawOverlays(this)) {              // 다른앱 위에 그리기 체크
                TedPermission.with(this)
                    .setPermissionListener(object : PermissionListener {
                        override fun onPermissionGranted() {
                            mToast("권한이 허용되었습니다.")
                        }

                        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                            deniedPermissions?.let {
                                mToast("설정화면에서 권한을 허용할수 있습니다.")
                            }
                        }
                    })
                    .setRationaleTitle("권한 확인")
                    .setRationaleMessage(getString(R.string.alert_permission_guide))
                    .setRationaleConfirmText("확인")
                    .setPermissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
                    .check()
            }
        }
    }

    fun setAlarm(alarm: Alarm) {
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
                    this,
                    alarm.id!!,
                    Intent(this, AlarmBroadcastReceiver::class.java).apply {
                        putExtra(
                            AlarmActivity.EXTRA_ALARM,
                            Bundle().apply { putParcelable(AlarmActivity.EXTRA_ALARM, alarm) })
                    },
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

            val info: AlarmManager.AlarmClockInfo = AlarmManager.AlarmClockInfo(triggerTime, null)
            getAlarmManager().setAlarmClock(info, pendingIntent)
            //절전모드에서도 동작하기
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
//                getAlarmManager().setExactAndAllowWhileIdle(
//                    AlarmManager.RTC_WAKEUP,
//                    triggerTime,
//                    pendingIntent
//                )
//            else
//                getAlarmManager().setExact(
//                    AlarmManager.RTC_WAKEUP,
//                    triggerTime,
//                    pendingIntent
//                )
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    fun cancel(alarm: Alarm) {
        alarm.id ?: return

        val pendingIntent =
            PendingIntent.getBroadcast(
                this,
                alarm.id!!,
                Intent(this, AlarmBroadcastReceiver::class.java).apply {
                    putExtra(AlarmActivity.EXTRA_ALARM, alarm)
                },
                PendingIntent.FLAG_UPDATE_CURRENT
            )
        getAlarmManager().cancel(pendingIntent)
    }

    fun setVolume(volume: Int) {
        defaultMusicVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        defaultAlarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM)
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, volume, 0)
    }

    fun onVibrateStart() {
        val pattern = longArrayOf(1000, 1000, 1000, 1000, 1000, 1000)

        vibrate = true
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            vibrateManager.vibrate(pattern, 0)
        } else {
            vibrateManager.vibrate(VibrationEffect.createWaveform(pattern, 0))
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP ||
            keyCode == KeyEvent.KEYCODE_VOLUME_DOWN ||
            keyCode == KeyEvent.KEYCODE_VOLUME_MUTE
        ) {
            if (vibrate) {
                onVibrateStop()
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    fun onVibrateStop() {
        vibrate = false
        vibrateManager.cancel()
    }

    fun openPopupAnimation() {
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    fun closePopupAnimation() {
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    fun checkOpenInterstitial(): Boolean {
        val interval = MyPreferences.readInterstitialInterval(this)
        val lastTime = MyPreferences.readInterstitialLastTime(this)
        val currentTime = System.currentTimeMillis()
        val differenceTime = currentTime - lastTime

        logD("interval = $interval, differenceTime = $differenceTime")
        return differenceTime > interval
    }
}