package com.example.arventurepath.ui.in_game_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arventurepath.data.models.ArventureToPlay
import com.example.arventurepath.data.models.Stop
import com.example.arventurepath.data.remote.DataProvider
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

    private val _win = MutableStateFlow(false)
    val win: StateFlow<Boolean> = _win

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

            } else {
                _win.emit(true)
            }
        }
    }

    fun removeStop() {
        stops.removeAt(0)
    }

}