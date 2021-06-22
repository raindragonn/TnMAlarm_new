package com.bluepig.tnmalarm.ui.edit

import android.content.Intent
import android.util.Log
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.bluepig.tnmalarm.R
import com.bluepig.tnmalarm.databinding.ActivityEditBinding
import com.bluepig.tnmalarm.lib.TnMAdsImpl
import com.bluepig.tnmalarm.model.Alarm
import com.bluepig.tnmalarm.ui.alarm.AlarmActivity
import com.bluepig.tnmalarm.ui.base.BaseActivity
import com.bluepig.tnmalarm.utils.MyEncoder
import com.bluepig.tnmalarm.utils.MyPreferences
import com.bluepig.tnmalarm.utils.ext.logD
import com.bluepig.tnmalarm.utils.ext.mToast
import java.util.*

class EditActivity : BaseActivity<ActivityEditBinding>(R.layout.activity_edit) {
    companion object {
        const val EXTRA_URL = "EXTRA_URL"
        const val EXTRA_SONG_URL = "EXTRA_SONG_URL"
        const val EXTRA_TITLE = "EXTRA_TITLE"
        const val EXTRA_ALARM = "EXTRA_ALARM"
    }

    private val viewModel: EditViewModel by viewModels()

    private val isEdit: Boolean by lazy { intent.hasExtra(EXTRA_ALARM) }
    private val tnmAds: TnMAdsImpl by lazy { TnMAdsImpl(applicationContext) }
    override fun onCreate() {
        init()

        binding.apply {
            vm = viewModel

            sbVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(
                    seekBar: SeekBar?,
                    progress: Int,
                    fromUser: Boolean
                ) {
                    viewModel.onVolumeChange(progress)
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            swVibrate.setOnCheckedChangeListener { _, isChecked ->
                viewModel.onVibrateClick(isChecked)
            }

            tpTime.setOnTimeChangedListener { _, hourOfDay, minute ->
                viewModel.setDate(hourOfDay, minute)
            }

            tnmAds.bannerLoad(llRoot)
        }

        viewModel.apply {
            eventDelete.observe(this@EditActivity, {
                openDelete()
            })

            eventEdit.observe(this@EditActivity, {
                onUpdate(getDao())
                mfinish()
            })

            eventAlarmDisable.observe(this@EditActivity, {
                cancel(it)
            })

            eventSave.observe(this@EditActivity, {
                onSave(getDao())
                mfinish()
            })

            eventBack.observe(this@EditActivity, {
                openGoBack()
            })
            eventPreView.observe(this@EditActivity, {
                openPreView()
            })

            eventPlaylistError.observe(this@EditActivity, {
                mToast("플레이리스트는 불가능 합니다.")
                mfinish()
            })

            eventAlarm.observe(this@EditActivity, { onOff ->
                alarm.value?.let {
                    setAlarm(it)
                }
            })
        }
    }

    override fun onBackPressed() {
        openGoBack()
    }

    private fun openPreView() {

        startActivity(Intent(this, AlarmActivity::class.java).apply {
            putExtra(AlarmActivity.EXTRA_PREVIEW, viewModel.alarm.value)
        })

        openPopupAnimation()
    }

    private fun openGoBack() {
        alertDialog(R.string.alert, R.string.alert_back,
            {
                mfinish()
            },
            {})
    }

    private fun init() {
        if (isEdit) {
            setEdit()
        } else {
            setNew()
        }
    }

    private fun setEdit() {
        intent.extras?.getParcelable<Alarm>(EXTRA_ALARM)?.let {
            viewModel.setEditAlarm(it)
        }

        viewModel.alarm.observe(this, { alarm ->
            binding.apply {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                    alarm.date?.let {
                        tpTime.hour = it.hour
                        tpTime.minute = it.minute
                    }
                } else {
                    alarm.date?.let {
                        tpTime.currentHour = it.hour
                        tpTime.currentMinute = it.minute
                    }
                }
                alarm.volume?.let { sbVolume.progress = it }
                swVibrate.isChecked = alarm.vibrate
            }
        })
    }

    private fun setNew() {
        viewModel.setNewAlarm()

        //유튜브 - 공유로 넘어옴
        intent.extras?.getString(Intent.EXTRA_TEXT)?.let { url ->
            if(!url.contains("youtu.be")){
                mToast("유튜브 외에는 불가능 합니다.")
                mfinish()
                return@let
            }

            viewModel.setAlarmTubeType()
            viewModel.setUrl(url.substring(url.lastIndexOf("/") + 1))
            if(isNetworkConnected()){
                viewModel.getTubeTitle(url)
            }else{
                mToast("인터넷 연결을 확인해주세요.")
                mfinish()
            }
        } ?: run {
            viewModel.setAlarmSongType()
            intent.getStringExtra(EXTRA_URL)?.let { viewModel.setUrl(it) }
            intent.getStringExtra(EXTRA_SONG_URL)?.let { viewModel.setSongUrl(it) }
            intent.getStringExtra(EXTRA_TITLE)?.let { viewModel.setSongTitle(it) }
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M)
            viewModel.setDate(binding.tpTime.hour, binding.tpTime.minute)
        else
            viewModel.setDate(binding.tpTime.currentHour, binding.tpTime.currentMinute)
    }


    private fun openDelete() {
        alertDialog(title = R.string.alert, message = R.string.alert_delete, positiveClick = {
            try {
                viewModel.onAlarmDelete(getDao())
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mfinish()
        }, cancelClick = {

        })
    }

    override fun onNetworkChanged(connect: Boolean) {
        viewModel.setNetworkConnected(connect)
    }
}