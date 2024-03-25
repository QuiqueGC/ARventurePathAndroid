package com.example.arventurepath.ui.in_game_fragment

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arventurepath.data.models.Achievement
import com.example.arventurepath.data.models.ArventureToPlay
import com.example.arventurepath.data.models.Stop
import com.example.arventurepath.data.models.StoryFragment
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
    private val _storyFragment = MutableSharedFlow<StoryFragment>()
    val storyFragment: SharedFlow<StoryFragment> = _storyFragment

    private val _win = MutableSharedFlow<List<Achievement>>()
    val win: SharedFlow<List<Achievement>> = _win

    private val stops = mutableListOf<Stop>()
    private val storyFragments = mutableListOf<StoryFragment>()

    private var achievements = mutableListOf<Achievement>()

    fun getArventureDetail(idArventure: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val deferred =
                async {
                    _arventureDetail.emit(DataProvider.getArventureToPlay(idArventure))
                    stops.addAll(_arventureDetail.value.route.stops)
                    storyFragments.addAll(_arventureDetail.value.story.storyFragments)
                }
            deferred.await()
            _loading.emit(false)
        }
    }

    fun getListAchievements() {
        viewModelScope.launch(Dispatchers.IO) {
            achievements = DataProvider.getListAchievements() as MutableList<Achievement>
            Log.i(">", achievements.count().toString())
            achievements.forEach {
                Log.i(">", it.name)
                Log.i(">", it.img)
                Log.i(">", it.id.toString())
            }
        }
    }

    fun getStoryFragment() {
        viewModelScope.launch {
            if (storyFragments.isNotEmpty()) {
                _storyFragment.emit(storyFragments[0])
            }
        }
    }

    fun removeStoryFragment() {
        if (storyFragments.isNotEmpty()) {
            storyFragments.removeAt(0)
        }
    }

    fun getStop() {
        viewModelScope.launch {
            if (stops.isNotEmpty()) {
                _stop.emit(stops[0])

            } else {
                achievements.add(_arventureDetail.value.achievement)
                _win.emit(achievements)
            }
        }
    }

    fun removeStop() {
        if (stops.isNotEmpty()) {
            stops.removeAt(0)
        }
    }

}