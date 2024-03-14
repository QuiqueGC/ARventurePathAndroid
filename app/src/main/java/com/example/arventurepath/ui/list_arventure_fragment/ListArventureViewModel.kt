package com.example.arventurepath.ui.list_arventure_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arventurepath.data.models.ItemArventure
import com.example.arventurepath.data.remote.DataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ListArventureViewModel : ViewModel() {


    private val _listArventures = MutableStateFlow<List<ItemArventure>>(listOf())
    val listArventures: StateFlow<List<ItemArventure>> = _listArventures

    fun getListArventures() {
        viewModelScope.launch(Dispatchers.IO) {
            _listArventures.emit(DataProvider.getListArventures())
        }
    }
}