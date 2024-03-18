package com.example.arventurepath.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Story(
    var id: Int = 0,
    var name: String = "",
    var summary: String = "",
    var img: String = "",
    var storyFragments: List<StoryFragment> = listOf(),
) : Parcelable
