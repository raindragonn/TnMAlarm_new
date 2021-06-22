package com.bluepig.tnmalarm.ui.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bluepig.tnmalarm.Const
import com.bluepig.tnmalarm.model.SongResponse
import com.bluepig.tnmalarm.network.search.searchClient
import com.bluepig.tnmalarm.ui.base.BaseViewModel
import com.bluepig.tnmalarm.utils.MyEncoder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class SearchViewModel : BaseViewModel() {
    private var offset = 0
    private var sameText = false

    private val searchClient by searchClient()

    private val _songDetail = MutableLiveData<String>()
    val songDetail: LiveData<String>
        get() = _songDetail

    private val _songUrl = MutableLiveData<String>()
    val songUrl: LiveData<String>
        get() = _songUrl

    private val _songTitle = MutableLiveData<String>()
    val songTitle: LiveData<String>
        get() =_songTitle

    val query = MutableLiveData<String>()

    private val _response = MutableLiveData<List<SongResponse>>()
    val response: LiveData<List<SongResponse>>
        get() =_response

    fun onSearch() {
        _networkConnected.value?.let {
            if (!it) {
                onShowNetworkError()
                return
            }
        }

        sameText = false
        viewModelScope.launch {
            val query = query.value ?: return@launch
            offset = 0
            showLoading()
            try {
                val response = searchClient.search(query, offset)
                val list = mutableListOf<SongResponse>()
                list.addAll(response.documents)
                _response.value = list

            } catch (e: Exception) {
                hideLoading()
            }
        }
    }

    fun onLoadMore() {
        _networkConnected.value?.let {
            if (!it) {
                onShowNetworkError()
                return
            }
        }

        sameText = true
        viewModelScope.launch {
            val query = query.value ?: return@launch
            showLoading()
            try {
                response.value?.toMutableList()?.let {
                    offset = it.size + 1
                    val response = searchClient.search(query, offset)
                    it.addAll(response.documents)
                    _response.value = it
                }
            }catch (e : java.lang.Exception){
                hideLoading()
            }
        }
    }

    fun onSongDetail(url: String, title: String) {
        _networkConnected.value?.let {
            if (!it) {
                onShowNetworkError()
                return
            }
        }

        viewModelScope.launch {
            _songTitle.value = title
            showLoading()

            val data = async(Dispatchers.IO) {
                val doc: Document = Jsoup.connect(url).get()
                val data = doc.select(MyEncoder.decodeText(Const.DETAIL_GET))
                    .attr(MyEncoder.decodeText(Const.DETAIL_ATTR))
                data
            }

            data.await().let {
                _songUrl.value = url
                _songDetail.value = it
                hideLoading()
            }
        }
    }


    override fun setNetworkConnected(connect: Boolean) {
        super.setNetworkConnected(connect)

        if (!connect) {
            onShowNetworkError()
        }
    }
}