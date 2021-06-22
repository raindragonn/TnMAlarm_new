package com.bluepig.tnmalarm.network.search

import com.bluepig.tnmalarm.model.TubeResponse
import com.bluepig.tnmalarm.network.NetworkManager
import retrofit2.create

fun tubeClient() = lazy { TubeClient() }

class TubeClient {
    private val searchApi by lazy { NetworkManager.tubeRetrofit.create<TubeApi>() }
    suspend fun search(
        url: String,
    ): TubeResponse {
        return searchApi.getList(url)
    }
}