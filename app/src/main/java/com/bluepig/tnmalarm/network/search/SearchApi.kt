package com.bluepig.tnmalarm.network.search

import com.bluepig.tnmalarm.model.SearchResponse
import retrofit2.http.GET
import retrofit2.http.Query


interface SearchApi {
    @GET("files.json")
    suspend fun getList(
        @Query("query") query: String,
        @Query("category") category : Int = 1,
        @Query("view") view : String = "web",
        @Query("offset") offset : Int = 0,
        @Query("limit") limit: Int = 20
    ): SearchResponse
}