package com.bluepig.tnmalarm.ui.alarm

import android.media.*
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.WindowManager
import androidx.activity.viewModels
import com.bluepig.tnmalarm.R
import com.bluepig.tnmalarm.databinding.ActivityAlarmBinding
import com.bluepig.tnmalarm.lib.TnMAdsImpl
import com.bluepig.tnmalarm.model.Alarm
import com.bluepig.tnmalarm.model.AlarmType
import com.bluepig.tnmalarm.player.YoutubePlayerCallBack
import com.bluepig.tnmalarm.ui.base.BaseActivity
import com.bluepig.tnmalarm.utils.MyPreferences
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer

class AlarmActivity : BaseActivity<ActivityAlarmBinding>(R.layout.activity_alarm) {

    companion object {
        const val EXTRA_PREVIEW = "EXTRA_PREVIEW"
        const val EXTRA_ALARM = "EXTRA_ALARM"
    }

    private val viewModel: AlarmViewModel by viewModels()
    private val myHandler: AlarmHandler by lazy { AlarmHandler(mainLooper) }
    private var isPreView = false
    private var ringtone: Ringtone? = null

    private fun getRingtone(): Ringtone? {
        if (ringtone == null) {
            var uri = MyPreferences.readDefaultRingTone(this)

            if (uri.isEmpty()) {
                RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALARM)?.let {
                    MyPreferences.writeDefaultRingTone(this, it.toString())
                    uri = it.toString()
                } ?: run {
                    return null
                }
            }

            ringtone = RingtoneManager.getRingtone(
                this,
                Uri.parse(uri)
            ).apply {
                audioAttributes =
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
            }
        }

        return ringtone
    }

    private val tnmAds: TnMAdsImpl by lazy {
        TnMAdsImpl(
            applicationContext,
            null,
            tnmAdsClosedListener
        )
    }

    private val tnmAdsClosedListener: () -> Unit = {
        mfinish()
    }


    override fun onCreate() {
        setAlarmFlags()
        tnmAds.interstitialLoad()

        binding.apply {
            vm = viewModel
            lifecycle.addObserver(yp)
        }

        myHandler.setTimerCallback {
            viewModel.onTimeSet(it)
        }

        viewModel.apply {
            eventBack.observe(this@AlarmActivity, {
                if (vibrate) {
                    onVibrateStop()
                }

                defaultMusicVolume?.let { setVolume(it) }
                myHandler.messageEnd()
                binding.yp.release()

                if (isPreView && checkOpenInterstitial() && tnmAds.isInterstitialLoaded()) {
                    getSongPlayer().releasePlayer()
                    tnmAds.interstitialShow()
                } else {
                    mfinish()
                }
            })

            alarm.observe(this@AlarmActivity, { alarm ->
                alarm.apply {
                    title?.let { it -> setTitle(it) }
                    volume?.let { setVolume(it) }

                    if (vibrate) {
                        onVibrateStart()
                    }

                    if (!isNetworkConnected()) {
                        hideLoading()
                        getRingtone()?.play() ?: this@AlarmActivity.run {
                            binding.tvSongTitle.visibility = View.VISIBLE
                            setSongTitle("인터넷상태를 확인해 주세요. \n 기본 알람음이\n 없습니다.")
                        }
                        return@observe
                    }

                    if (type == AlarmType.YOUTUBE.toInt()) {
                        binding.yp.addYouTubePlayerListener(object : YoutubePlayerCallBack() {
                            override fun onReady(youTubePlayer: YouTubePlayer) {
                                hideLoading()
                                binding.yp.visibility = View.VISIBLE
                                url?.let { url -> youTubePlayer.loadVideo(url, 0F) }
                            }

                            override fun onError(
                                youTubePlayer: YouTubePlayer,
                                error: PlayerConstants.PlayerError
                            ) {
                                super.onError(youTubePlayer, error)
                                hideLoading()
                                getRingtone()?.play() ?: run {
                                    binding.tvSongTitle.visibility = View.VISIBLE
                                    setSongTitle("인터넷상태를 확인해 주세요. \n 기본 알람음이\n 없습니다.")
                                }

                                binding.yp.visibility = View.GONE
                            }
                        })
                    } else {
                        songTitle?.let { it -> setSongTitle(it) }
                        url?.let { it ->
                            getSongPlayer().createPlayer(applicationContext)
                            getSongPlayer().addLoadingCallBack { isLoading ->
                                if (isLoading) {
                                    hideLoading()
                                    binding.tvSongTitle.visibility = View.VISIBLE
                                }
                            }

                            getSongPlayer().addErrorCallBack {
                                getSongPlayer().releasePlayer()
                                songUrl?.let { songUrl -> onEventReload(songUrl) } ?: run {
                                    hideLoading()
                                    getRingtone()?.play() ?: run {
                                        binding.tvSongTitle.visibility = View.VISIBLE
                                        setSongTitle("인터넷상태를 확인해 주세요. \n 기본 알람음이\n 없습니다.")
                                    }
                                }
                            }

                            getSongPlayer().loadUrl(it)
                            getSongPlayer().pauseAndPlay()
                        }
                    }
                }

            })

            eventRetry.observe(this@AlarmActivity, {
                getSongPlayer().createPlayer(applicationContext)
                getSongPlayer().addLoadingCallBack { isLoading ->
                    if (isLoading) {
                        hideLoading()
                        binding.tvSongTitle.visibility = View.VISIBLE
                    }
                }
                getSongPlayer().addErrorCallBack {
                    getSongPlayer().releasePlayer()
                    hideLoading()

                    getRingtone()?.play() ?: run {
                        binding.tvSongTitle.visibility = View.VISIBLE
                        setSongTitle("인터넷상태를 확인해 주세요. \n 기본 알람음이\n 없습니다.")
                    }
                }

                getSongPlayer().loadUrl(it)
                getSongPlayer().pauseAndPlay()
            })
        }

        init()

        if (!isPreView) {
            viewModel.alarmDisable(getDao())
        }
    }

    override fun onBackPressed() {
        viewModel.onBackClick()
    }

    override fun onDestroy() {
        viewModel.getSongPlayer().releasePlayer()
        getRingtone()?.let {
            if (it.isPlaying) it.stop()
        }
//        ringtone = null
        super.onDestroy()
    }

    private fun init() {
        myHandler.messageStart()
        viewModel.showLoading()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.requestAudioFocus(
                AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
                    .setAudioAttributes(
                        AudioAttributes.Builder()
                            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                            .setUsage(AudioAttributes.USAGE_MEDIA)
                            .build()
                    )
                    .setAcceptsDelayedFocusGain(true)
                    .setOnAudioFocusChangeListener {}
                    .build()
            )
        } else {
            audioManager.requestAudioFocus(
                null,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }

        if (intent.hasExtra(EXTRA_PREVIEW)) {
            preViewSetting()
            return
        }

        if (intent.hasExtra(EXTRA_ALARM)) {
            alarmSetting()
            return
        }

    }

    private fun preViewSetting() {
        intent.getParcelableExtra<Alarm>(EXTRA_PREVIEW)?.let {
            viewModel.setAlarm(it)
        }
        isPreView = true
    }

    private fun alarmSetting() {
        intent.getParcelableExtra<Alarm>(EXTRA_ALARM)?.let {
            viewModel.setAlarm(it)
        }
        isPreView = false
    }

    override fun onNetworkChanged(connect: Boolean) {
        viewModel.setNetworkConnected(connect)
    }

    override fun onPause() {
        super.onPause()

        getRingtone()?.let {
            if (it.isPlaying)
                it.stop()
        }

        if (vibrate) {
            onVibrateStop()
        }
    }


    private fun setAlarmFlags() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true) // 잠금화면에서도 window를 보여줍니다.
            setTurnScreenOn(true)   // 꺼진화면을 켜줍니다.
            keyguardManager.requestDismissKeyguard(this, null)
            //window가 attach되면 잠금해제를 하는 flag.
            //Activity Context를 가진 윈도우매니저에서만 동작
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED     // 잠금화면에서도 window를 보여줍니다.
                        or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD // window가 attach되면 잠금해제를 하는 flag
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON // 꺼진화면을 켜줍니다.
                        or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON

            )
        }
    }
}