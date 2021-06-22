package com.bluepig.tnmalarm.model

import com.google.gson.annotations.SerializedName

data class AwsResponse(
    @SerializedName("useAdMob")
    val useAdMob : Int,
    @SerializedName("interstitialInterval")
    val interstitialInterval : Long
)
