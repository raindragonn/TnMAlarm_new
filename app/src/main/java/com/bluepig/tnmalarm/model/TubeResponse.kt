package com.bluepig.tnmalarm.model

import com.google.gson.annotations.SerializedName

data class TubeResponse(
    @SerializedName("title")
    val title : String,
    @SerializedName("author_url")
    val author_url : String
){
    override fun toString(): String {
        return "title = $title author_url = $author_url"
    }
}
