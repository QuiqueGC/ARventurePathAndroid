package com.example.arventurepath.data.remote

import com.example.arventurepath.data.models.ArventureDetail
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


    suspend fun getArventureDetail(idArventure: Int): ArventureDetail {
        val arventureResponse = remoteApiService.getArventureDetail(idArventure).body()!!
        return ArventureDetail(
            arventureResponse.id ?: 0,
            arventureResponse.name ?: "",
            transformTime(arventureResponse.routeResponse?.time ?: ""),
            transformDistance(arventureResponse.routeResponse?.distance ?: 0.0),
            arventureResponse.storyResponse?.img ?: "",
            arventureResponse.storyResponse?.summary ?: "",
            arventureResponse.routeResponse?.stop?.get(0)?.name ?: "",
            arventureResponse.storyResponse?.name ?: ""


        )
    }

    private fun transformDistance(distanceToTransform: Double): String {
        return String.format("%.2f", distanceToTransform) + " km"
    }

    private fun transformTime(timeToTransform: String): String {
        return timeToTransform.substring(0, 2) + "h " + timeToTransform.substring(3, 5) + "min"
    }


}