package com.example.arventurepath.data.remote.retrofit

import com.example.arventurepath.data.remote.responses.ArventuresResponse
import retrofit2.Response
import retrofit2.http.GET

interface RemoteApiService {
    @GET("arventures")
    suspend fun getListArventures(): Response<List<ArventuresResponse>>
}