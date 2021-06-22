package com.bluepig.tnmalarm.player

import android.content.Context
import com.google.android.exoplayer2.ExoPlayer

interface SongPlayer {
    fun loadUrl(url: String)
    fun createPlayer(context: Context): ExoPlayer
    fun releasePlayer()
    fun pauseAndPlay()
    fun pause()
    fun addLoadingCallBack(callBack : (Boolean) -> Unit)
    fun addErrorCallBack(callBack : () -> Unit)
}