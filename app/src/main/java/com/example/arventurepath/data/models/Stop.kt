package com.example.arventurepath.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Stop(
    val id: Int = 0,
    val name: String = "",
    var longitude: Double = 0.0,
    var latitude: Double = 0.0,
) : Parcelable
