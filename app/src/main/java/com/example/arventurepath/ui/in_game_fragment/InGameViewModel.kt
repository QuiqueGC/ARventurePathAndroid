package com.example.arventurepath.ui.in_game_fragment

import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arventurepath.data.models.ArventureToPlay
import com.example.arventurepath.data.models.Stop
import com.example.arventurepath.data.remote.DataProvider
import com.example.arventurepath.utils.MyLocationServices
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

    private val _stop = MutableSharedFlow<Stop>()
    val stop: SharedFlow<Stop> = _stop

    private val myLocationServices = MyLocationServices()

    //private var myLocation: Location? = null
    private val _myLocation: MutableSharedFlow<Location> = MutableSharedFlow()
    val myLocation: SharedFlow<Location> = _myLocation

    private val stops = mutableListOf<Stop>()

    fun getArventureDetail(idArventure: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val deferred =
                async {
                    _arventureDetail.emit(DataProvider.getArventureToPlay(idArventure))
                    stops.addAll(_arventureDetail.value.route.stops)
                }
            deferred.await()
            _loading.emit(false)
        }
    }

    fun getStop() {
        viewModelScope.launch {
            if (stops.isNotEmpty()) {
                _stop.emit(stops[0])
                stops.removeAt(0)
            }
        }
    }

    fun getMyLocation(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val myLocation = myLocationServices.getUserLocation(context)
            if (myLocation != null) {
                _myLocation.emit(myLocation)
            }
        }
    }
}