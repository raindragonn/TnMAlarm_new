package com.bluepig.tnmalarm.network

import com.bluepig.tnmalarm.Config
import com.bluepig.tnmalarm.Const
import com.bluepig.tnmalarm.utils.MyEncoder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object NetworkManager {

    val awsRetrofit : Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(MyEncoder.decodeText(Const.SERVER_SC))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val searchRetrofit : Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(MyEncoder.decodeText(Const.SEARCH_SC))
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val tubeRetrofit : Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(Const.YOUTUBE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val client : OkHttpClient by lazy {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

        if(Config.DEBUG){
            OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build()
        }else{
            OkHttpClient.Builder()
                .build()
        }
    }
}