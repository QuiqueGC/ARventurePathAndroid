package com.example.arventurepath.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class UserToPlay(
    val id: Int = 0,
    var name: String = "",
    val mail: String = "",
    val passwd: String = "",
    val img: String = "",
    val distance: Double = 0.0,
    val steps: Int = 0,
    val achievement: MutableList<Achievement> = mutableListOf()
) : Parcelable
