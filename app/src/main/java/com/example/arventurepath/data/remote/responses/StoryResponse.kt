package com.example.arventurepath.data.remote.responses

import com.google.gson.annotations.SerializedName

data class StoryResponse(
    @SerializedName("arventure") var arventure: ArrayList<ArventuresResponse>?,
    @SerializedName("fragment") var fragment: ArrayList<FragmentResponse>?,
    @SerializedName("happening") var happening: ArrayList<HappeningResponse>?,
    @SerializedName("id") var id: Int?,
    @SerializedName("name") var name: String?,
    @SerializedName("img") var img: String?,
    @SerializedName("summary") var summary: String?
)
