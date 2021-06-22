package com.bluepig.tnmalarm.lib

import android.content.Context
import android.util.Base64
import android.view.ViewGroup
import com.bluepig.tnmalarm.Config
import com.bluepig.tnmalarm.utils.MyPreferences
import com.bluepig.tnmalarm.utils.ext.logD
import com.google.android.gms.ads.*

class TnMAdsImpl(
    context: Context,
    loadedListener: (() -> Unit)? = null,
    closedListener: (() -> Unit)? = null,
    mFailedListener: (() -> Unit)? = null
) : TnMAds {
    private var useAdmob: Boolean = MyPreferences.readUseAdmob(context)
    private var mInterstitialAd: InterstitialAd
    private var mBannerAd: AdView

    private external fun interstitialAd(): String
    private external fun bannerAd(): String

    private val interstitialBase: ByteArray by lazy {
        Base64.decode(
            interstitialAd(),
            Base64.NO_WRAP
        )
    }

    private val bannerBase: ByteArray by lazy { Base64.decode(bannerAd(), Base64.NO_WRAP) }

    private fun getInterstitialAd(): String {
        return when (Config.DEBUG) {
            true -> "ca-app-pub-3940256099942544/1033173712"
            false -> String(interstitialBase)
        }
    }

    private fun getBannerAd(): String {
        return when (Config.DEBUG) {
            true -> "ca-app-pub-3940256099942544/6300978111"
            false -> String(bannerBase)
        }
    }

    init {
        System.loadLibrary("mk")
        mInterstitialAd = InterstitialAd(context)
        mInterstitialAd.adUnitId = getInterstitialAd()
        mInterstitialAd.adListener = object : AdListener() {
            override fun onAdClosed() {
                MyPreferences.writeInterstitialLastTime(context,System.currentTimeMillis())
                closedListener?.invoke()
            }

            override fun onAdLoaded() {
                loadedListener?.invoke()
            }

            override fun onAdFailedToLoad(loadAdError: LoadAdError?) {
                super.onAdFailedToLoad(loadAdError)
                loadAdError?.message?.let { logD(it) }
                mFailedListener?.invoke()
            }
        }

        mBannerAd = AdView(context)
        mBannerAd.adSize = AdSize.BANNER
        mBannerAd.adUnitId = getBannerAd()
    }

    override fun isInterstitialLoaded(): Boolean = mInterstitialAd.isLoaded
    override fun interstitialLoad() {
        if (useAdmob) {
            mInterstitialAd.loadAd(AdRequest.Builder().build())
        }
    }

    override fun interstitialShow() {
        if (mInterstitialAd.isLoaded) {
            mInterstitialAd.show()
        }
    }

    override fun bannerLoad(containerView: ViewGroup) {
        if (useAdmob) {
            mBannerAd.loadAd(AdRequest.Builder().build())
            mBannerAd.adListener = object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    containerView.addView(mBannerAd)
                }
            }
        }
    }
}