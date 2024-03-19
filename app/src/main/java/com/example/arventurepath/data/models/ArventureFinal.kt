package com.example.arventurepath.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArventureFinal(
    val name: String = "",
    val time: String = "",
    val distance: String = "",
    val img: String = "",
    val steps: Int = 0,
    val estimateTime: String = "",
    val storyName: String = "",
    val stops: List<Stop> = listOf(),
    val achievements: List<Achievement> = listOf()
) : Parcelable
