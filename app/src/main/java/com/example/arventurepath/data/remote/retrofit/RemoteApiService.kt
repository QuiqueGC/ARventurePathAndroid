package com.example.arventurepath.data.remote.retrofit

import com.example.arventurepath.data.models.UserToRegister
import com.example.arventurepath.data.remote.responses.AchievementResponse
import com.example.arventurepath.data.remote.responses.ArventuresResponse
import com.example.arventurepath.data.remote.responses.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface RemoteApiService {
    @GET("arventures")
    suspend fun getListArventures(): Response<List<ArventuresResponse>>

    @GET("arventures/{idArventure}")
    suspend fun getArventureById(
        @Path("idArventure") idArventure: Int
    ): Response<ArventuresResponse>

    @GET("achievements")
    suspend fun getListAchievements(
    ): Response<List<AchievementResponse>>

    @GET("users")
    suspend fun getListUsers(
    ): Response<List<UserResponse>>

    @GET("users/{idUser}")
    suspend fun getUserById(
        @Path("idUser") idUser: Int
    ): Response<UserResponse>

    @POST("users")
    suspend fun registerUser(
        @Body userToRegister: UserToRegister
    ): Response<UserResponse>


}