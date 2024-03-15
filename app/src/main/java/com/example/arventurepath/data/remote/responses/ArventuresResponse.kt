package com.example.arventurepath.data.remote.responses

import com.google.gson.annotations.SerializedName

data class ArventuresResponse(
    @SerializedName("achievement") var achievement: String?,
    @SerializedName("route") var routeResponse: RouteResponse?,
    @SerializedName("story") var storyResponse: StoryResponse?,
    @SerializedName("happening") var happening: ArrayList<HappeningResponse>?,
    @SerializedName("user") var user: ArrayList<UserResponse>?,
    @SerializedName("id") var id: Int?,
    @SerializedName("name") var name: String?,
    @SerializedName("idRoute") var idRoute: Int?,
    @SerializedName("idStory") var idStory: Int?,
    @SerializedName("idAchievement") var idAchievement: Int?
)
