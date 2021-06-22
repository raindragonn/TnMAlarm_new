package com.bluepig.tnmalarm.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bluepig.tnmalarm.player.SongPlayerImpl
import com.bluepig.tnmalarm.ui.base.BaseViewModel
import com.bluepig.tnmalarm.utils.EventLiveData

class SongDialogViewModel : BaseViewModel() {
    private val _songPlayer = SongPlayerImpl()

    private val _title = MutableLiveData("제목")
    val title: LiveData<String>
        get() = _title

    private val _url = MutableLiveData<String>()
    val url: LiveData<String>
        get() = _url

    private val _songUrl = MutableLiveData<String>()
    val songUrl: LiveData<String>
        get() = _songUrl

    private val _playing = MutableLiveData(false)
    val playing: LiveData<Boolean>
        get() = _playing

    val eventCancel: EventLiveData<Boolean> = EventLiveData()
    val eventSelect: EventLiveData<Boolean> = EventLiveData()

    fun getSongPlayer() = _songPlayer

    fun setTitle(nTitle: String) {
        _title.value = nTitle
    }

    fun setUrl(url: String) {
        this._url.value = url
    }

    fun setSongUrl(url: String) {
        this._songUrl.value = url
    }

    fun loadUrl() {
        _url.value?.let {
            _songPlayer.loadUrl(it)
        }
    }

    fun release() {
        _playing.value = false
        _songPlayer.releasePlayer()
    }

    fun pause() {
        _playing.value = false
        _songPlayer.pause()
    }

    fun pauseAndPlay() {
        _songPlayer.pauseAndPlay()
        _playing.value = !_playing.value!!
    }

    fun onCancel() {
        eventCancel.setEventValue(true)
    }

    fun onSelect() {
        eventSelect.setEventValue(true)
    }


}

