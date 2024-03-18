package com.example.arventurepath.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryFragment(
    val id: Int = 0,
    val ordinal: Int = 0,
    val content: String = ""
) : Parcelable
