package com.example.arventurepath.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ItemArventure(
    val id: Int = -1,
    val title: String = "",
    val summary: String = "",
    val time: String = "",
    val distance: String = "",
    val img: String = ""
) : Parcelable