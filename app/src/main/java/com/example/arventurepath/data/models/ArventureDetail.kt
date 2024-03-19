package com.example.arventurepath.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArventureDetail(
    val id: Int = 0,
    val name: String = "",
    val time: String = "",
    val distance: String = "",
    val img: String = "",
    val summary: String = "",
    val origin: String = "",
    val nameStory: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
) : Parcelable
