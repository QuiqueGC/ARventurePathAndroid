package com.example.arventurepath.data.remote.retrofit

import com.example.arventurepath.data.remote.responses.ArventuresListResponse
import retrofit2.Response
import retrofit2.http.GET

interface RemoteApiService {
    @GET("arventures")
    suspend fun getArventuresList(): Response<List<ArventuresListResponse>>
}