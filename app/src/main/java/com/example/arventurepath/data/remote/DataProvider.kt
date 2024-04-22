package com.example.arventurepath.data.remote

import android.util.Log
import com.example.arventurepath.data.models.Achievement
import com.example.arventurepath.data.models.ArventureDetail
import com.example.arventurepath.data.models.ArventureFinal
import com.example.arventurepath.data.models.ArventureToPlay
import com.example.arventurepath.data.models.Happening
import com.example.arventurepath.data.models.ItemArventure
import com.example.arventurepath.data.models.Route
import com.example.arventurepath.data.models.Stop
import com.example.arventurepath.data.models.Story
import com.example.arventurepath.data.models.StoryFragment
import com.example.arventurepath.data.models.UserToPlay
import com.example.arventurepath.data.models.UserToRegister
import com.example.arventurepath.data.remote.responses.AchievementResponse
import com.example.arventurepath.data.remote.responses.HappeningResponse
import com.example.arventurepath.data.remote.responses.RouteResponse
import com.example.arventurepath.data.remote.responses.StoryResponse
import com.example.arventurepath.data.remote.retrofit.RetrofitClient

object DataProvider {

    private val remoteApiService = RetrofitClient.getApiServices()


    suspend fun getListArventures(): List<ItemArventure> {
        val arventuresListResponse = remoteApiService.getListArventures().body()!!
        val arventuresList = mutableListOf<ItemArventure>()

        arventuresListResponse.forEach { arventureResponse ->
            arventuresList.add(
                ItemArventure(
                    arventureResponse.id ?: -1,
                    arventureResponse.name ?: "",
                    arventureResponse.storyResponse?.summary ?: "",
                    transformTime(arventureResponse.routeResponse?.time ?: ""),
                    transformDistance(arventureResponse.routeResponse?.distance ?: 0.0),
                    arventureResponse.storyResponse?.img ?: ""
                )
            )
        }
        return arventuresList
    }
    suspend fun getListUsers(): List<UserToRegister>{
        val usersListResponse = remoteApiService.getListUsers().body()!!
        val usersList = mutableListOf<UserToRegister>()

        usersListResponse.forEach {userResponse ->
        usersList.add(
            UserToRegister(
                    userResponse.id ?: 0,
                    userResponse.name ?: "",
                    userResponse.mail ?: "",
                    userResponse.passwd ?: "",
                    userResponse.img ?: "",
                    userResponse.distance ?: 0.0,
                    userResponse.steps ?: -1
                )
            )
        }
        return usersList
    }

    suspend fun getListAchievements(): List<Achievement> {
        val achievements = mutableListOf<Achievement>()
        val achievementsResponse = remoteApiService.getListAchievements().body()!!
        if (!achievementsResponse.isNullOrEmpty()) {
            achievementsResponse.forEach {
                if (it.arventure.isNullOrEmpty()) {
                    achievements.add(
                        Achievement(
                            it.id ?: 0,
                            it.name ?: "",
                            it.img ?: ""
                        )
                    )
                }
            }
            achievements.removeAll { it.name.contains("Completa la aventura:") }
            achievements.forEach {
                Log.i(">", it.name)
            }
        }
        return achievements
    }

    suspend fun getArventureDetail(idArventure: Int): ArventureDetail {
        val arventureResponse = remoteApiService.getArventureById(idArventure).body()!!
        return ArventureDetail(
            arventureResponse.id ?: 0,
            arventureResponse.name ?: "",
            transformTime(arventureResponse.routeResponse?.time ?: ""),
            transformDistance(arventureResponse.routeResponse?.distance ?: 0.0),
            arventureResponse.storyResponse?.img ?: "",
            arventureResponse.storyResponse?.summary ?: "",
            arventureResponse.routeResponse?.stop?.get(0)?.name ?: "",
            arventureResponse.storyResponse?.name ?: "",
            arventureResponse.routeResponse?.stop?.get(0)?.latitude ?: 0.0,
            arventureResponse.routeResponse?.stop?.get(0)?.longitude ?: 0.0,
        )
    }
    suspend fun getArventureScore(idArventure: Int): ArventureFinal {
        val arventuresResponse = remoteApiService.getArventureById(idArventure).body()!!
        val route = takeRouteFromArventureResponse(arventuresResponse.routeResponse)
        val achievement = takeAchievementFromArventureResponse(arventuresResponse.achievement)

        return ArventureFinal(
            arventuresResponse.id ?: 0,
            arventuresResponse.name ?: "",
            transformTime(arventuresResponse.routeResponse?.time ?: ""),
            transformDistance(arventuresResponse.routeResponse?.distance ?: 0.0),
            arventuresResponse.storyResponse?.img ?: "",
            0,
            "",
            arventuresResponse.storyResponse?.name ?: "",
            route.stops,
            mutableListOf(achievement)
        )
    }

    private fun transformDistance(distanceToTransform: Double): String {
        return String.format("%.2f", distanceToTransform) + " km"
    }

    private fun transformTime(timeToTransform: String): String {
        return timeToTransform.substring(0, 2) + "h " + timeToTransform.substring(3, 5) + "min"
    }

    suspend fun registerUser(userToRegister: UserToRegister): UserToPlay {
        val userResponse = remoteApiService.registerUser(userToRegister).body()!!
        val achievements = mutableListOf<Achievement>()

        if (!userResponse.achievement.isNullOrEmpty()) {
            userResponse.achievement!!.forEach {
                achievements.add(
                    Achievement(
                        it.id ?: 0,
                        it.name ?: "",
                        it.img ?: ""
                    )
                )
            }
        }

        return UserToPlay(
            userResponse.id ?: 0,
            userResponse.name ?: "",
            userResponse.mail ?: "",
            userResponse.passwd ?: "",
            userResponse.img ?: "",
            userResponse.distance ?: 0.0,
            userResponse.steps ?: 0,
            achievements
        )
    }
    suspend fun getUserById(idUser: Int): UserToPlay {
        val userResponse = remoteApiService.getUserById(idUser).body()!!
        val achievements = mutableListOf<Achievement>()
        if (!userResponse.achievement.isNullOrEmpty()) {
            userResponse.achievement!!.forEach {
                achievements.add(
                    Achievement(
                        it.id ?: 0,
                        it.name ?: "",
                        it.img ?: "",
                    )
                )
            }
        }

        return UserToPlay(
            userResponse.id ?: 0,
            userResponse.name ?: "",
            userResponse.mail ?: "",
            userResponse.passwd ?: "",
            userResponse.img ?: "",
            userResponse.distance ?: 0.0,
            userResponse.steps ?: 0,
            achievements
        )
    }

    suspend fun updateUser(idUser: Int, user: UserToPlay){
        remoteApiService.updateUser(idUser, user)
    }

    suspend fun getArventureToPlay(idArventure: Int): ArventureToPlay {
        val arventureResponse = remoteApiService.getArventureById(idArventure).body()!!

        val route = takeRouteFromArventureResponse(arventureResponse.routeResponse)
        val story = takeStoryFromArventureResponse(arventureResponse.storyResponse)
        val achievement = takeAchievementFromArventureResponse(arventureResponse.achievement)
        val happenings = takeHappeningsFromArventureResponse(arventureResponse.happening)

        return ArventureToPlay(
            arventureResponse.id ?: 0,
            arventureResponse.name ?: "",
            route,
            story,
            achievement,
            happenings
        )
    }

    private fun takeHappeningsFromArventureResponse(happeningsResponse: ArrayList<HappeningResponse>?): List<Happening> {
        val happenings = mutableListOf<Happening>()
        if (!happeningsResponse.isNullOrEmpty()) {
            happeningsResponse.forEach {
                happenings.add(
                    Happening(
                        it.id ?: 0,
                        it.name ?: "",
                        it.text ?: "",
                        it.type ?: "",
                        it.url ?: "",
                    )
                )
            }
        }
        return happenings
    }

    private fun takeAchievementFromArventureResponse(achievementResponse: AchievementResponse?): Achievement {
        return if (achievementResponse != null) {
            Achievement(
                achievementResponse.id ?: 0,
                achievementResponse.name ?: "",
                achievementResponse.img ?: ""
            )
        } else {
            Achievement()
        }
    }

    private fun takeStoryFromArventureResponse(storyResponse: StoryResponse?): Story {
        val storyFragments = mutableListOf<StoryFragment>()
        if (!storyResponse?.fragment.isNullOrEmpty()) {
            storyResponse?.fragment?.forEach {
                storyFragments.add(
                    StoryFragment(
                        it.id ?: 0,
                        it.ordinal ?: 0,
                        it.content ?: ""
                    )
                )
            }
        }
        return if (storyResponse != null) {
            Story(
                storyResponse.id ?: 0,
                storyResponse.name ?: "",
                storyResponse.summary ?: "",
                storyResponse.img ?: "",
                storyFragments
            )
        } else {
            Story()
        }
    }

    private fun takeRouteFromArventureResponse(routeResponse: RouteResponse?): Route {
        val stops = mutableListOf<Stop>()

        if (!routeResponse?.stop.isNullOrEmpty()) {
            routeResponse?.stop?.forEach {
                stops.add(
                    Stop(
                        it.id ?: 0,
                        it.name ?: "",
                        it.longitude ?: 0.0,
                        it.latitude ?: 0.0
                    )
                )
            }
        }
        return if (routeResponse != null) {
            Route(
                routeResponse.id ?: 0,
                routeResponse.name ?: "",
                routeResponse.time ?: "",
                routeResponse.steps ?: 0,
                routeResponse.distance ?: 0.0,
                stops
            )
        } else {
            Route()
        }

    }

}