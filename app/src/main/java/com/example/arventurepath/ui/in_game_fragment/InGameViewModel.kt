package com.example.arventurepath.ui.in_game_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arventurepath.data.models.Achievement
import com.example.arventurepath.data.models.ArventureToPlay
import com.example.arventurepath.data.models.ListAchievements
import com.example.arventurepath.data.models.Stop
import com.example.arventurepath.data.models.StoryFragment
import com.example.arventurepath.data.models.UserToPlay
import com.example.arventurepath.data.remote.DataProvider
import com.example.arventurepath.data.remote.retrofit.RemoteApiService
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

    private val _win = MutableSharedFlow<ListAchievements>()
    val win: SharedFlow<ListAchievements> = _win

    private val stops = mutableListOf<Stop>()
    private val storyFragments = mutableListOf<StoryFragment>()

    private var achievements = ListAchievements(mutableListOf())
    private val achievementsToEarn = mutableListOf<Achievement>()

    private val _achievementToShow = MutableSharedFlow<Achievement>()
    val achievementToShow: SharedFlow<Achievement> = _achievementToShow

    private var _user = UserToPlay()
    fun getArventureDetailUser(idArventure: Int,idUser: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val deferred =
                async {
                    _arventureDetail.emit(DataProvider.getArventureToPlay(idArventure))
                    stops.addAll(_arventureDetail.value.route.stops)
                    storyFragments.addAll(_arventureDetail.value.story.storyFragments)
                    _user = DataProvider.getUserById(idUser)
                }
            deferred.await()
            getListAchievements()
            _loading.emit(false)
        }
    }

    private fun getListAchievements() {
        viewModelScope.launch(Dispatchers.IO) {
            achievementsToEarn.addAll(DataProvider.getListAchievements() as MutableList<Achievement>)
            achievementsToEarn.removeAll{ achievement -> achievement in _user.achievements  }
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
                _user.achievements.add(_arventureDetail.value.achievement)
                achievements.achievements.add(_arventureDetail.value.achievement)
                _win.emit(achievements)
                DataProvider.updateUser(_user.id, _user)
            }
        }
    }

    fun removeStop() {
        if (stops.isNotEmpty()) {
            stops.removeAt(0)
        }
    }

    fun earnTimeAchievement() {
        _user.achievements.addAll(achievementsToEarn.filter { it.name == "Juega durante 5 minutos" })
        achievements.achievements.addAll(achievementsToEarn.filter { it.name == "Juega durante 5 minutos" })
        viewModelScope.launch(Dispatchers.IO) {
            _achievementToShow.emit(achievements.achievements.last())
        }
    }

    fun earn100StepsAchievement() {
        _user.achievements.addAll(achievementsToEarn.filter { it.name == "Recorre 100 pasos" })
        achievements.achievements.addAll(achievementsToEarn.filter { it.name == "Recorre 100 pasos" })
        viewModelScope.launch(Dispatchers.IO) {
            _achievementToShow.emit(achievements.achievements.last())
            DataProvider.updateUser(_user.id, _user)
        }
    }

    fun earn500StepsAchievement() {
        _user.achievements.addAll(achievementsToEarn.filter { it.name == "Recorre 500 pasos" })
        achievements.achievements.addAll(achievementsToEarn.filter { it.name == "Recorre 500 pasos" })
        viewModelScope.launch(Dispatchers.IO) {
            _achievementToShow.emit(achievements.achievements.last())
        }
    }

}