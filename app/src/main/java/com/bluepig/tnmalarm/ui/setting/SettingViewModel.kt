package com.bluepig.tnmalarm.ui.setting

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bluepig.tnmalarm.ui.base.BaseViewModel
import com.bluepig.tnmalarm.utils.EventLiveData

class SettingViewModel : BaseViewModel() {

    private val _version : MutableLiveData<String> = MutableLiveData()
    private val _ringtone : MutableLiveData<String> = MutableLiveData()

    val version : LiveData<String> get() = _version
    val ringtone : LiveData<String> get() = _ringtone

    val eventVersionName = EventLiveData<Boolean>()
    val eventOpenSourceLibrary = EventLiveData<Boolean>()
    val eventSelectRingTone = EventLiveData<Boolean>()
    val eventPermission = EventLiveData<Boolean>()
    val eventTutorial = EventLiveData<Boolean>()

    fun setVersionName(name : String){
        _version.value = name
    }

    fun setRingtoneName(name: String){
        _ringtone.value = name
    }

    fun onEventVersionClick(){
        eventVersionName.setEventValue(true)
    }

    fun onEventOpenSourceLibrary(){
        eventOpenSourceLibrary.setEventValue(true)
    }

    fun onEventRingtoneSelect(){
        eventSelectRingTone.setEventValue(true)
    }

    fun onEventPermission(){
        eventPermission.setEventValue(true)
    }

    fun onEventTutorial(){
        eventTutorial.setEventValue(true)
    }


}