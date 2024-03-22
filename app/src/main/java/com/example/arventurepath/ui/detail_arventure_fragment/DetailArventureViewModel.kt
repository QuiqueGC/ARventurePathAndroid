package com.example.arventurepath.ui.detail_arventure_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arventurepath.data.models.ArventureDetail
import com.example.arventurepath.data.remote.DataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class DetailArventureViewModel : ViewModel() {

    private val _arventureDetail = MutableStateFlow(ArventureDetail())
    val arventureDetail: StateFlow<ArventureDetail> = _arventureDetail

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading


    fun getArventureDetail(idArventure: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val deferred =
                async {
                    _arventureDetail.emit(DataProvider.getArventureDetail(idArventure))
                }
            deferred.await()
            _loading.emit(false)
        }
    }
}