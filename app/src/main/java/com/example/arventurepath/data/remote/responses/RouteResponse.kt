package com.example.arventurepath.data.remote.responses

import com.google.gson.annotations.SerializedName

data class RouteResponse(
    @SerializedName("arventure") var arventure: ArrayList<ArventuresResponse>?,
    @SerializedName("stop") var stop: ArrayList<StopResponse>?,
    @SerializedName("id") var id: Int?,
    @SerializedName("time") var time: String?,
    @SerializedName("steps") var steps: Int?,
    @SerializedName("distance") var distance: Double?,
    @SerializedName("name") var name: String?
)
