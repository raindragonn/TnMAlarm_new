package com.bluepig.tnmalarm.player

import android.content.Context
import android.net.Uri
import com.bluepig.tnmalarm.R
import com.bluepig.tnmalarm.utils.ext.logD
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.source.ExtractorMediaSource
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util

class SongPlayerImpl : SongPlayer {
    private lateinit var exoPlayer: ExoPlayer
    private lateinit var context: Context
    private var loadingCallBack: ((Boolean) -> Unit)? = null
    private var errorCallBack: (() -> Unit)? = null

    override fun createPlayer(context: Context): ExoPlayer {
        this.context = context
        initializePlayer()
        logD("createPlayer")
        return exoPlayer
    }

    private fun initializePlayer() {
        logD("initializePlayer")
        exoPlayer = ExoPlayerFactory.newSimpleInstance(
            context,
            DefaultRenderersFactory(context),
            DefaultTrackSelector(),
            DefaultLoadControl()
        )
    }

    override fun loadUrl(url: String) {
        logD("loadUrl")
        if (!::exoPlayer.isInitialized) {
            return
        }

        val userAgent = Util.getUserAgent(context, context.getString(R.string.app_name))
        val mediaSource = ExtractorMediaSource
            .Factory(DefaultDataSourceFactory(context, userAgent))
            .setExtractorsFactory(DefaultExtractorsFactory())
            .createMediaSource(Uri.parse(url))

        exoPlayer.prepare(mediaSource)
        exoPlayer.playWhenReady = false
        exoPlayer.addListener(object : Player.EventListener {
            override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
                logD("exoplayer_state_change")
                logD("$playbackState")
                if (playWhenReady && playbackState == Player.STATE_READY) {
                    loadingCallBack?.invoke(true)
                }
            }

            override fun onPlayerError(error: ExoPlaybackException?) {
                errorCallBack?.invoke()
                error?.stackTraceToString()?.let { logD(it) }
            }
        })
    }

    override fun addLoadingCallBack(callBack: (Boolean) -> Unit) {
        loadingCallBack = callBack
    }

    override fun addErrorCallBack(callBack: () -> Unit) {
        errorCallBack = callBack
    }


    override fun pauseAndPlay() {
        if (!::exoPlayer.isInitialized) {
            return
        }
        exoPlayer.playWhenReady = !exoPlayer.playWhenReady
    }

    override fun releasePlayer() {
        if (!::exoPlayer.isInitialized) {
            return
        }

        exoPlayer.stop()
        exoPlayer.release()
    }

    override fun pause() {
        if (!::exoPlayer.isInitialized) {
            return
        }

        exoPlayer.playWhenReady = false
    }
}