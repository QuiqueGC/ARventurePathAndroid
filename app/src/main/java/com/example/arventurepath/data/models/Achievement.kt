package com.example.arventurepath.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Achievement(
    val id: Int = 0,
    val name: String = "",
    val img: String = ""
) : Parcelable
