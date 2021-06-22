package com.bluepig.tnmalarm.model

import com.google.gson.annotations.SerializedName

data class SearchResponse(
    @SerializedName("items")
    val documents : List<SongResponse>,
    @SerializedName("totalCount")
    val totalCount : Int
)
