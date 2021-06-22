package com.bluepig.tnmalarm.ui.main

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.lifecycle.*
import com.bluepig.tnmalarm.Const
import com.bluepig.tnmalarm.R
import com.bluepig.tnmalarm.databinding.ActivityMainBinding
import com.bluepig.tnmalarm.lib.TnMAdsImpl
import com.bluepig.tnmalarm.model.Alarm
import com.bluepig.tnmalarm.network.aws.awsClient
import com.bluepig.tnmalarm.ui.base.BaseActivity
import com.bluepig.tnmalarm.ui.edit.EditActivity
import com.bluepig.tnmalarm.ui.search.SearchActivity
import com.bluepig.tnmalarm.ui.setting.SettingActivity
import com.bluepig.tnmalarm.utils.MyPreferences
import com.bluepig.tnmalarm.utils.ext.mToast
import com.google.firebase.analytics.FirebaseAnalytics

class MainActivity : BaseActivity<ActivityMainBinding>(R.layout.activity_main) {
    private val viewModel: MainViewModel by viewModels()

    private val adapter by lazy {
        MainAlarmAdapter(
            rootClickListener,
            onOffClickListener
        ).apply { setHasStableIds(true) }
    }
    private val tnmAds: TnMAdsImpl by lazy {
        TnMAdsImpl(
            applicationContext,
            closedListener = searchInterstitialClosedListener
        )
    }

    private val searchInterstitialClosedListener: () -> Unit = {
        openAct(SearchActivity::class.java, false)
        tnmAds.interstitialLoad()
    }

    private val rootClickListener: (Alarm) -> Unit = { item ->
        openEdit(item)
    }

    private val onOffClickListener: (Alarm) -> Unit = { item ->
        viewModel.updateData(getDao(), item)
    }

    override fun onCreate() {
        MyPreferences.writeDefaultRingTone(applicationContext, "")
        binding.apply {
            vm = viewModel
            rv.adapter = adapter

            loadBanner(llRoot)
            tnmAds.interstitialLoad()
        }

        viewModel.apply {
            getData(getDao())

            eventNetworkError.observe(this@MainActivity, {
                mToast(R.string.toast_network_check)
            })

            eventTube.observe(this@MainActivity, {
                openTube()
            })

            eventSearch.observe(this@MainActivity, {
                FirebaseAnalytics.getInstance(this@MainActivity)
                    .logEvent(FirebaseAnalytics.Event.SELECT_ITEM, Bundle().apply {
                        putString(FirebaseAnalytics.Param.CONTENT_TYPE, "검색")
                    })

                if (checkOpenInterstitial() &&
                    tnmAds.isInterstitialLoaded()
                ) {
                    tnmAds.interstitialShow()
                } else {
                    openAct(SearchActivity::class.java, false)
                }
            })

            eventAlarm.observe(this@MainActivity, {
                if (it.onOff)
                    setAlarm(it)
                else
                    cancel(it)
            })
            eventSetting.observe(this@MainActivity, {
                openAct(SettingActivity::class.java, false)
            })
        }
        checkPermission()
    }

    private fun loadBanner(containerView: ViewGroup) {
        tnmAds.bannerLoad(containerView)
    }

    override fun onNetworkChanged(connect: Boolean) {
        viewModel.setNetworkConnected(connect)
    }

    private fun openTube() {
        FirebaseAnalytics.getInstance(this)
            .logEvent(FirebaseAnalytics.Event.SELECT_ITEM, Bundle().apply {
                putString(FirebaseAnalytics.Param.CONTENT_TYPE, "튜브")
            })

        try {
            startActivity(Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(Const.YOUTUBE_URL)
                setPackage(Const.YOUTUBE_PACKAGE)
            })
            openPopupAnimation()

        } catch (e: Exception) {
            mToast(R.string.youtube_need)

            startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(Const.MARKET_URL + Const.YOUTUBE_PACKAGE)
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
            )
            openPopupAnimation()
        }
    }

    private fun openEdit(alarm: Alarm) {
        startActivity(Intent(this, EditActivity::class.java).apply {
            putExtra(EditActivity.EXTRA_ALARM, alarm)
        })

        openPopupAnimation()


    }


}