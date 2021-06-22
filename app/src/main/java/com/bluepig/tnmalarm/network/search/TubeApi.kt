package com.bluepig.tnmalarm.network.search

import com.bluepig.tnmalarm.model.SearchResponse
import com.bluepig.tnmalarm.model.TubeResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface TubeApi {
    @GET("oembed")
    suspend fun getList(
        @Query("url") url: String,
        @Query("format") format : String = "json",
    ): TubeResponse
}