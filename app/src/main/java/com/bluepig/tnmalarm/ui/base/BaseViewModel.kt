package com.bluepig.tnmalarm.ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bluepig.tnmalarm.utils.EventLiveData

abstract class BaseViewModel : ViewModel() {

    private val _loading: MutableLiveData<Boolean> = MutableLiveData<Boolean>(false)
    val loading: LiveData<Boolean>
        get() = _loading

    protected val _networkConnected = MutableLiveData<Boolean>()

    val eventNetworkError = EventLiveData<Boolean>()
    val eventBack = EventLiveData<Boolean>()

    fun showLoading() {
        _loading.value = true
    }

    fun hideLoading() {
        _loading.value = false
    }

    open fun setNetworkConnected(connect: Boolean) {
        _networkConnected.value = connect
    }

    protected fun onShowNetworkError() {
        eventNetworkError.setEventValue(true)
    }

    fun onBackClick(){
        eventBack.setEventValue(true)
    }
}
