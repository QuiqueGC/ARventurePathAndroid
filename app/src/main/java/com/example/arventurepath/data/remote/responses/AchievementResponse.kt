package com.example.arventurepath.data.remote.responses

import com.google.gson.annotations.SerializedName

data class AchievementResponse(
    @SerializedName("id") var id: Int?,
    @SerializedName("name") var name: String?,
    @SerializedName("img") var img: String?,
    @SerializedName("arventure") var arventure: ArrayList<ArventuresResponse>?,
    @SerializedName("user") var user: ArrayList<UserResponse>?
)
