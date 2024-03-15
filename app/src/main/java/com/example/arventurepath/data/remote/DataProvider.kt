package com.example.arventurepath.data.remote

import com.example.arventurepath.data.models.ItemArventure
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

    private fun transformDistance(distanceToTransform: Double): String {
        return String.format("%.2f", distanceToTransform) + " km"
    }

    private fun transformTime(timeToTransform: String): String {
        return timeToTransform.substring(0, 2) + "h " + timeToTransform.substring(3, 5) + "min"
    }
}