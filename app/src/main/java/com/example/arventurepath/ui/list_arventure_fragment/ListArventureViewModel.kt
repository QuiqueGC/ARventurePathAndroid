package com.example.arventurepath.ui.list_arventure_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arventurepath.data.models.ItemArventure
import com.example.arventurepath.data.remote.DataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ListArventureViewModel : ViewModel() {


    private val _listArventures = MutableStateFlow<List<ItemArventure>>(listOf())
    val listArventures: StateFlow<List<ItemArventure>> = _listArventures
    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    fun getListArventures() {
        viewModelScope.launch(Dispatchers.IO) {
            val deferred = async { _listArventures.emit(DataProvider.getListArventures()) }
            deferred.await()
            _loading.emit(false)
        }
    }
}