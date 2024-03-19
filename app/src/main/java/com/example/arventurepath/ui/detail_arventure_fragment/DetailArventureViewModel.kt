package com.example.arventurepath.ui.detail_arventure_fragment

import android.content.Context
import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arventurepath.data.models.ArventureDetail
import com.example.arventurepath.data.remote.DataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailArventureViewModel : ViewModel() {

    private val _arventureDetail = MutableStateFlow(ArventureDetail())
    val arventureDetail: StateFlow<ArventureDetail> = _arventureDetail

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading


    private val myLocationServices = MyLocationServices()
    private var myLocation: Location? = null
    private val _isNear = MutableSharedFlow<Boolean>()
    val isNear: SharedFlow<Boolean> = _isNear

    fun getArventureDetail(idArventure: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val deferred =
                async { _arventureDetail.emit(DataProvider.getArventureDetail(idArventure)) }
            deferred.await()
            _loading.emit(false)
        }
    }

    fun getLocation(context: Context) {

        viewModelScope.launch(Dispatchers.IO) {
            myLocation = myLocationServices.getUserLocation(context)
            if (myLocation != null &&
                checkIfIsNear(myLocation!!.latitude, _arventureDetail.value.latitude) &&
                checkIfIsNear(myLocation!!.longitude, _arventureDetail.value.longitude)
            ) {
                _isNear.emit(true)
            } else {
                _isNear.emit(false)
            }
        }
    }

    private fun checkIfIsNear(myPosition: Double, destinyPosition: Double): Boolean {
        return myPosition > destinyPosition - 0.005 && myPosition < destinyPosition + 0.005
    }
}