package com.example.arventurepath.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArventureToPlay(
    var id: Int = 0,
    var name: String = "",
    val time: String = "",
    val distance: String = "",
    val img: String = "",
    val summary: String = "",
    val origin: String = "",
    val nameStory: String = "",
) : Parcelable
