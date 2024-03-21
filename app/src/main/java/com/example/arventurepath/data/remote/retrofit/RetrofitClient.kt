package com.example.arventurepath.data.remote.retrofit

import com.example.arventurepath.utils.Constants.BASE_URL
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.Executors

object RetrofitClient {
    private val retrofit: Retrofit
    private val remoteApiService: RemoteApiService
    init {
        val gson = GsonBuilder().setLenient().create()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .callbackExecutor(Executors.newSingleThreadExecutor())
            .build()
        remoteApiService = retrofit.create(RemoteApiService::class.java)
    }

    fun getApiServices(): RemoteApiService {
        return remoteApiService
    }
}