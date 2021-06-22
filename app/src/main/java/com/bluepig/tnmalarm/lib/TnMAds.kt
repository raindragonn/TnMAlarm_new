package com.bluepig.tnmalarm.lib

import android.view.ViewGroup

interface TnMAds {
    fun interstitialLoad()
    fun isInterstitialLoaded(): Boolean
    fun interstitialShow()
    fun bannerLoad(containerView: ViewGroup)
}