package com.example.arventurepath.data.remote.responses

import com.google.gson.annotations.SerializedName

data class HappeningResponse(
    @SerializedName("id") var id: Int?,
    @SerializedName("name") var name: String?,
    @SerializedName("text") var text: String?,
    @SerializedName("type") var type: String?,
    @SerializedName("url") var url: String?,
    @SerializedName("idStory") var idStory: Int?,
    @SerializedName("arventure") var arventure: ArrayList<ArventuresResponse>?
)
