package com.example.arventurepath.data.remote.responses

import com.google.gson.annotations.SerializedName

data class FragmentResponse(
    @SerializedName("id") var id: Int?,
    @SerializedName("ordinal") var ordinal: Int?,
    @SerializedName("idStory") var idStory: Int?,
    @SerializedName("content") var content: String?
)
