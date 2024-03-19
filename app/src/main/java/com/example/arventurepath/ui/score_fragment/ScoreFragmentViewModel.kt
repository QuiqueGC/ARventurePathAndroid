package com.example.arventurepath.ui.score_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arventurepath.data.models.ArventureFinal
import com.example.arventurepath.data.remote.DataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ScoreFragmentViewModel : ViewModel() {

    private val _arventureFinal = MutableStateFlow(ArventureFinal())
    val arventureFinal: StateFlow<ArventureFinal> = _arventureFinal

    private val _loading = MutableStateFlow(true)
    val loading: StateFlow<Boolean> = _loading

    fun getArventureScore(idArventure: Int){
        viewModelScope.launch(Dispatchers.IO) {
            val deferred = async {
                val response = DataProvider.getArventureScore(idArventure)
                _arventureFinal.emit(response)
            }
            deferred.await()
            _loading.emit(false)
        }
    }
}