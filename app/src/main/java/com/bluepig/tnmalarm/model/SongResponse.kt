package com.bluepig.tnmalarm.model

import com.google.gson.annotations.SerializedName

data class SongResponse(
    @SerializedName("d1PageUrl")
    val url : String,
    @SerializedName("fileName")
    val title : String,
    @SerializedName("thumbnailUrl")
    val thumbnail : String,
    @SerializedName("fileId")
    val fileId : String
){
    override fun toString(): String {
        return "url = $url  fileId = $fileId title = $title  thumbnail = $thumbnail  fileId = $fileId"
    }
}
