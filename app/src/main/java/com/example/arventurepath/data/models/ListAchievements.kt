package com.example.arventurepath.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ListAchievements(
    val achievements: MutableList<Achievement>
) : Parcelable
