package com.example.arventurepath.data.remote.responses

import com.google.gson.annotations.SerializedName

data class StopResponse(
    @SerializedName("id") var id: Int?,
    @SerializedName("name") var name: String?,
    @SerializedName("longitude") var longitude: Double?,
    @SerializedName("latitude") var latitude: Double?,
    @SerializedName("idRoute") var idRoute: Int?

)
