package com.bluepig.tnmalarm.ui.edit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bluepig.tnmalarm.model.Alarm
import com.bluepig.tnmalarm.model.AlarmType
import com.bluepig.tnmalarm.model.Weak
import com.bluepig.tnmalarm.network.search.tubeClient
import com.bluepig.tnmalarm.room.AlarmDAO
import com.bluepig.tnmalarm.ui.base.BaseViewModel
import com.bluepig.tnmalarm.utils.EventLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.threeten.bp.LocalTime

class EditViewModel : BaseViewModel() {
    private val tubeClient by tubeClient()
    private var isUpdate = false // 알람을 수정인지 추가인지
    var title = MutableLiveData<String>()

    private val _alarm: MutableLiveData<Alarm> = MutableLiveData()
    private val _songTitle: MutableLiveData<String> = MutableLiveData()
    private val _date: MutableLiveData<LocalTime> = MutableLiveData()
    private val _weak: MutableLiveData<Weak> = MutableLiveData()
    private val _url: MutableLiveData<String> = MutableLiveData()
    private val _songUrl: MutableLiveData<String> = MutableLiveData()
    private val _type: MutableLiveData<Int> = MutableLiveData()
    private val _volume: MutableLiveData<Int> = MutableLiveData(15)
    private val _vibrate: MutableLiveData<Boolean> = MutableLiveData(true)

    val alarm: LiveData<Alarm> get() = _alarm
    val songTitle: LiveData<String> get() = _songTitle
    val weak: LiveData<Weak> get() = _weak
    val url: LiveData<String> get() = _url
    val type: LiveData<Int> get() = _type
    val volume: LiveData<Int> get() = _volume
    val vibrate: LiveData<Boolean> get() = _vibrate

    val eventDelete = EventLiveData<Boolean>()
    val eventSave = EventLiveData<Boolean>()
    val eventEdit = EventLiveData<Boolean>()
    val eventPreView = EventLiveData<Boolean>()
    val eventPlaylistError = EventLiveData<Boolean>()
    val eventAlarm = EventLiveData<Boolean>()
    val eventAlarmDisable = EventLiveData<Alarm>()

    fun setNewAlarm() {
        isUpdate = false

        _alarm.value = Alarm(
            title = _alarm.value?.title ?: "",
            date = _date.value,
            weak = _weak.value ?: kotlin.run {
                _weak.value = Weak()
                Weak()
            },
            url = _url.value,
            type = _type.value,
            volume = _volume.value ?: 15,
            songTitle = _songTitle.value ?: "",
            vibrate = _vibrate.value ?: true,
            songUrl = _songUrl.value
        )
    }

    fun onSaveClick() {
        if (isUpdate)
            eventEdit.setEventValue(true)
        else
            eventSave.setEventValue(true)
    }

    fun setEditAlarm(alarm: Alarm) {
        isUpdate = true
        _alarm.value = alarm
        setEdit()
    }

    fun setEdit() {
        _alarm.value?.apply {
            _songTitle.value = songTitle
            _date.value = date
            _weak.value = weak
            _url.value = url
            _type.value = type
            this@EditViewModel.title.value = title
            _volume.value = volume
            _vibrate.value = vibrate
            _songUrl.value = songUrl
        }
    }


    fun onSave(dao: AlarmDAO) = viewModelScope.launch {
        _alarm.value?.apply {
            songTitle = _songTitle.value ?: ""
            date = _date.value
            weak = _weak.value!!
            url = _url.value!!
            type = _type.value!!
            title = this@EditViewModel.title.value ?: ""
            volume = _volume.value!!
            vibrate = _vibrate.value!!
            onOff = true
            songUrl = _songUrl.value
        }?.let {
            withContext(Dispatchers.IO) {
                dao.insert(it).apply {
                    it.id = this.toInt()
                }

                withContext(Dispatchers.Main) {
                    eventAlarm.setEventValue(true)
                }
            }
        }
    }

    fun onUpdate(dao: AlarmDAO) = viewModelScope.launch {
        _alarm.value?.apply {
            songTitle = _songTitle.value ?: ""
            date = _date.value
            weak = _weak.value!!
            url = _url.value!!
            type = _type.value!!
            title = this@EditViewModel.title.value!!
            volume = _volume.value!!
            vibrate = _vibrate.value!!
            onOff = true
            songUrl = _songUrl.value
        }?.let {
            withContext(Dispatchers.IO) {
                dao.update(it)

                withContext(Dispatchers.Main) {
                    eventAlarmDisable.setEventValue(it)
                    eventAlarm.setEventValue(true)
                }

            }
        }
    }

    fun onVolumeChange(progress: Int) {
        _volume.value = progress
    }

    fun setAlarmTubeType() {
        _type.value = AlarmType.YOUTUBE.toInt()
    }

    fun setAlarmSongType() {
        _type.value = AlarmType.SONG.toInt()
    }

    fun setSongTitle(title: String) {
        _songTitle.value = title
    }

    fun getTubeTitle(url: String) = viewModelScope.launch {
        tubeClient.search(url).apply {
            _songTitle.value = title
            if (!author_url.contains("www.youtube.com")) {
                eventPlaylistError.setEventValue(true)
            }
        }
    }

    fun setUrl(nUrl: String) {
        _url.value = nUrl
    }

    fun setSongUrl(nUrl: String) {
        _songUrl.value = nUrl
    }

    fun setDate(hour: Int, minute: Int) {
        _date.value = LocalTime.of(hour, minute)
    }

    fun onVibrateClick(isChecked: Boolean) {
        _vibrate.value = isChecked
    }

    fun onDeleteClick() {
        eventDelete.setEventValue(true)
    }


    fun onPreViewClick() {
        _alarm.value?.apply {
            songTitle = _songTitle.value ?: ""
            date = _date.value
            weak = _weak.value!!
            url = _url.value!!
            type = _type.value!!
            title = this@EditViewModel.title.value ?: ""
            volume = _volume.value!!
            vibrate = _vibrate.value!!
            onOff = true
            songUrl = _songUrl.value
        }

        eventPreView.setEventValue(true)
    }

    fun onAlarmDelete(dao: AlarmDAO) = viewModelScope.launch(Dispatchers.IO) {
        _alarm.value?.let {
            dao.delete(it)
            withContext(Dispatchers.Main) {
                eventAlarmDisable.setEventValue(it)
            }
        }
    }

    fun onWeakClick(click: Int) {
        when (click) {
            1 -> {
                _weak.value = _weak.value?.apply { onMondayClick() }
            }
            2 -> {
                _weak.value = _weak.value?.apply { onTuesdayClick() }
            }
            3 -> {
                _weak.value = _weak.value?.apply { onWednesdayClick() }
            }
            4 -> {
                _weak.value = _weak.value?.apply { onThursdayClick() }
            }
            5 -> {
                _weak.value = _weak.value?.apply { onFridayClick() }
            }
            6 -> {
                _weak.value = _weak.value?.apply { onSaturdayClick() }
            }
            7 -> {
                _weak.value = _weak.value?.apply { onSunDayClick() }
            }
        }
    }


}