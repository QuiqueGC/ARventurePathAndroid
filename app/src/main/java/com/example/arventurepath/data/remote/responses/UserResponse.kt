package com.example.arventurepath.data.remote.responses

import com.google.gson.annotations.SerializedName

data class UserResponse(
    @SerializedName("id") var id: Int?,
    @SerializedName("name") var name: String?,
    @SerializedName("mail") var mail: String?,
    @SerializedName("passwd") var passwd: String?,
    @SerializedName("img") var img: String?,
    @SerializedName("distance") var distance: Double?,
    @SerializedName("steps") var steps: Int?,
    @SerializedName("achievement") var achievement: ArrayList<AchievementResponse>?,
    @SerializedName("arventure") var arventure: ArrayList<ArventuresResponse>?

)
