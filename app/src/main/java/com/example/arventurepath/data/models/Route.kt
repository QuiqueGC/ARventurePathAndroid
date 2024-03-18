package com.example.arventurepath.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Route(
    val id: Int = 0,
    val name: String = "",
    val time: String = "",
    val steps: Int = 0,
    val distance: Double = 0.0,
    val stops: List<Stop> = listOf(),

    ) : Parcelable
