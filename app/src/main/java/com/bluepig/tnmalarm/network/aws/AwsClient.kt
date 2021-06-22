package com.bluepig.tnmalarm.network.aws

import com.bluepig.tnmalarm.model.AwsResponse
import com.bluepig.tnmalarm.network.NetworkManager
import retrofit2.create

fun awsClient() = lazy { AwsClient() }

class AwsClient {
    private val awsApi by lazy { NetworkManager.awsRetrofit.create<AwsApi>() }

    suspend fun getSetting(
        key: String
    ): AwsResponse {
        return awsApi.getSetting(key)
    }
}