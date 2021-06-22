package com.bluepig.tnmalarm.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bluepig.tnmalarm.model.Alarm
import com.bluepig.tnmalarm.room.AlarmDAO
import com.bluepig.tnmalarm.ui.base.BaseViewModel
import com.bluepig.tnmalarm.utils.EventLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel : BaseViewModel() {

    var alarmList: LiveData<List<Alarm>>? = null

    private val _fabClick: MutableLiveData<Boolean> = MutableLiveData(false)
    val fabClick: LiveData<Boolean>
        get() = _fabClick

    fun onFabClick() {
        _fabClick.value = !_fabClick.value!!
    }

    val eventTube = EventLiveData<Boolean>()
    val eventSearch = EventLiveData<Boolean>()
    val eventSetting = EventLiveData<Boolean>()
    val eventAlarm = EventLiveData<Alarm>()


    override fun setNetworkConnected(connect: Boolean) {
        super.setNetworkConnected(connect)
        if (!connect) {
            onShowNetworkError()
        }
    }

    fun getData(dao: AlarmDAO) {
        alarmList = dao.getAll()
    }

    fun updateData(dao: AlarmDAO, alarm: Alarm) = viewModelScope.launch {
        withContext(Dispatchers.IO) {
            dao.update(alarm.apply {
                onOff = !onOff

                withContext(Dispatchers.Main){
                    eventAlarm.setEventValue(this@apply)
                }
            })
        }

    }

    fun onSearch() {
        eventSearch.setEventValue(true)
    }

    fun onTube() {
        eventTube.setEventValue(true)
    }
    fun onSetting(){
        eventSetting.setEventValue(true)
    }

}