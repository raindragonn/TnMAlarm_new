package com.bluepig.tnmalarm.network.aws

import com.bluepig.tnmalarm.model.AwsResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface AwsApi {
    @FormUrlEncoded
    @POST("login.php")
    suspend fun getSetting(
        @Field("key") key: String
    ): AwsResponse
}