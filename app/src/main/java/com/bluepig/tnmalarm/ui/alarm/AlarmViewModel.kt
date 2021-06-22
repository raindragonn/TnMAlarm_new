package com.bluepig.tnmalarm.ui.alarm

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bluepig.tnmalarm.Config
import com.bluepig.tnmalarm.Const
import com.bluepig.tnmalarm.model.Alarm
import com.bluepig.tnmalarm.player.SongPlayerImpl
import com.bluepig.tnmalarm.room.AlarmDAO
import com.bluepig.tnmalarm.ui.base.BaseViewModel
import com.bluepig.tnmalarm.utils.EventLiveData
import com.bluepig.tnmalarm.utils.MyEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class AlarmViewModel : BaseViewModel() {
    private val _songPlayer = SongPlayerImpl()

    private val _alarm: MutableLiveData<Alarm> = MutableLiveData()
    private val _time: MutableLiveData<String> = MutableLiveData()
    private val _title: MutableLiveData<String> = MutableLiveData()
    private val _songTitle: MutableLiveData<String> = MutableLiveData()

    val alarm: LiveData<Alarm> get() = _alarm
    val time: LiveData<String> get() = _time
    val title: LiveData<String> get() = _title
    val songTitle: LiveData<String> get() = _songTitle

    val eventError: EventLiveData<Boolean> = EventLiveData()
    val eventRetry: EventLiveData<String> = EventLiveData()
    val eventDebug: EventLiveData<Bundle> = EventLiveData()

    val eventAlarmDisable = EventLiveData<Alarm>()

    val isDebug : Boolean = Config.DEBUG


    fun getSongPlayer() = _songPlayer

    fun onTimeSet(string: String) {
        _time.value = string
    }

    fun setAlarm(alarm: Alarm) {
        _alarm.value = alarm
    }

    fun setSongTitle(string: String) {
        _songTitle.value = string
    }

    fun setTitle(string: String) {
        if (string.isEmpty())
            _title.value = "Tube & Music Alarm"
        else
            _title.value = string
    }

    fun alarmDisable(dao: AlarmDAO) = viewModelScope.launch {
        _alarm.value?.apply {
            if (weak.toConvertString() == "반복없음") {
                onOff = false
            }
        }.run {
            withContext(Dispatchers.IO) {
                this@run?.let { dao.update(it) }
            }
        }
    }

    fun onEventReload(nUrl: String){
        viewModelScope.launch {
            val data = async(Dispatchers.IO) {
                val doc: Document = Jsoup.connect(nUrl).get()
                val data = doc.select(MyEncoder.decodeText(Const.DETAIL_GET))
                    .attr(MyEncoder.decodeText(Const.DETAIL_ATTR))
                data
            }

            data.await().let {
                eventRetry.setEventValue(it)
            }
        }
    }

    fun onDebugClick(bundle : Bundle?){
        bundle?.let {
            eventDebug.setEventValue(it)
        }
    }
}