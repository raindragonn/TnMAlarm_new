package com.bluepig.tnmalarm.network.search

import com.bluepig.tnmalarm.model.SearchResponse
import com.bluepig.tnmalarm.network.NetworkManager
import retrofit2.create

fun searchClient() = lazy { SearchClient() }

class SearchClient {
    private val searchApi by lazy { NetworkManager.searchRetrofit.create<SearchApi>() }
    suspend fun search(
        query: String,
        offset : Int
    ): SearchResponse {
        return searchApi.getList(query = query, offset = offset)
    }
}