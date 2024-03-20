package com.example.arventurepath.ui.in_game_fragment

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arventurepath.data.models.ArventureToPlay
import com.example.arventurepath.data.remote.DataProvider
import com.example.arventurepath.ui.detail_arventure_fragment.MyLocationServices
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class InGameViewModel() : ViewModel() {

    private val _arventureDetail = MutableStateFlow(ArventureToPlay())
    val arventureDetail: StateFlow<ArventureToPlay> = _arventureDetail

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    private val _latLng = MutableSharedFlow<LatLng>()
    private val latLng: SharedFlow<LatLng> = _latLng

    private val myLocationServices = MyLocationServices()
    private var myLocation: Location? = null

    fun getArventureDetail(idArventure: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val deferred =
                async {
                    _arventureDetail.emit(DataProvider.getArventureToPlay(idArventure))
                }
            deferred.await()
            _loading.emit(false)
        }
    }


}