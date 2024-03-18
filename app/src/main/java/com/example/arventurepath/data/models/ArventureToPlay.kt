package com.example.arventurepath.data.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class ArventureToPlay(
    val id: Int = 0,
    val name: String = "",
    val route: Route = Route(),
    val story: Story = Story(),
    val achievement: Achievement = Achievement(),
    val happenings: List<Happening> = listOf()
) : Parcelable
