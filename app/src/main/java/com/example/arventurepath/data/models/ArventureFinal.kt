package com.example.arventurepath.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArventureFinal(
    val id: Int = 0,
    val name: String = "",
    val time: String = "",
    val distance: String = "",
    val img: String = "",
    var steps: Int = 0,
    var estimateTime: String = "",
    val storyName: String = "",
    val stops: List<Stop> = listOf(),
    var achievements: List<Achievement> = listOf()
) : Parcelable
