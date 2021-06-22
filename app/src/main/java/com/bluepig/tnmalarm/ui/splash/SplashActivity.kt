package com.bluepig.tnmalarm.ui.splash

import androidx.activity.viewModels
import com.bluepig.tnmalarm.Config
import com.bluepig.tnmalarm.R
import com.bluepig.tnmalarm.databinding.ActivitySplashBinding
import com.bluepig.tnmalarm.ui.base.BaseActivity
import com.bluepig.tnmalarm.ui.main.MainActivity
import com.bluepig.tnmalarm.ui.tutorial.TutorialActivity
import com.bluepig.tnmalarm.utils.MyEncoder
import com.bluepig.tnmalarm.utils.MyPreferences
import java.util.*

class SplashActivity : BaseActivity<ActivitySplashBinding>(R.layout.activity_splash) {

    private val viewModel: SplashViewModel by viewModels()
    private fun isShowTutorial(): Boolean = MyPreferences.readShowTutorial(this)

    override fun onCreate() {
        binding.vm = viewModel
        if (Config.DEBUG) {
            MyPreferences.writeShowTutorial(this)
        }

        viewModel.apply {
            awsResponse.observe(this@SplashActivity) {
                with(it) {
                    MyPreferences.writeUseAdmob(this@SplashActivity, (useAdMob == 1))
                    MyPreferences.writeInterstitialInterval(
                        this@SplashActivity,
                        interstitialInterval * 1000L
                    )
                }
                open()
            }

            eventStart.observe(this@SplashActivity, {
                MyPreferences.writeUseAdmob(this@SplashActivity, false)
                MyPreferences.writeInterstitialInterval(this@SplashActivity)
                open()
            })

            if(isNetworkConnected()){
                getSetting(MyEncoder.getBluePig())
            }
            onEventStart()
        }
    }

    private fun open() {
        if (!isShowTutorial()) {
            openAct(TutorialActivity::class.java)
        } else {
            openAct(MainActivity::class.java)
        }
    }

    override fun onNetworkChanged(connect: Boolean) {
        viewModel.setNetworkConnected(connect)
    }

}