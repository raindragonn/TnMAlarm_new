package com.bluepig.tnmalarm.ui.splash

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bluepig.tnmalarm.model.AwsResponse
import com.bluepig.tnmalarm.network.aws.awsClient
import com.bluepig.tnmalarm.ui.base.BaseViewModel
import com.bluepig.tnmalarm.utils.EventLiveData
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashViewModel : BaseViewModel() {
    private val client by awsClient()

    private val _awsResponse: MutableLiveData<AwsResponse> = MutableLiveData()
    val awsResponse: LiveData<AwsResponse>
        get() = _awsResponse

    val eventStart: EventLiveData<Boolean> = EventLiveData()

    fun onEventStart() {
        viewModelScope.launch {
            delay(1000L)
            eventStart.setEventValue(true)
        }
    }

    fun getSetting(key: String) {
        viewModelScope.launch {
            val response = client.getSetting(key)
            _awsResponse.value = response
        }
    }

}