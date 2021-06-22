package com.bluepig.tnmalarm.ui.alarm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bluepig.tnmalarm.ui.base.BaseViewModel

class DebugDialogViewModel : BaseViewModel() {
    private val _debugState: MutableLiveData<String> = MutableLiveData()
    val debugState: LiveData<String> get() = _debugState

    fun setDebugState(nString : String){
        _debugState.value = nString
    }
}

