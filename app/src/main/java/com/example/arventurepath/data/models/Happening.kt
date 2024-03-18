package com.example.arventurepath.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Happening(
    val id: Int = 0,
    val name: String = "",
    val text: String = "",
    val type: String = "",
    val img: String = "",
) : Parcelable
