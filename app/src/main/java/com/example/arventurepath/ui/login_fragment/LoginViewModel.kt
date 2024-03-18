package com.example.arventurepath.ui.login_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arventurepath.data.models.ItemArventure
import com.example.arventurepath.data.models.UserToRegister
import com.example.arventurepath.data.remote.DataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {

    private var listUsers: List<UserToRegister> = listOf()


    fun getListUsers(){
        viewModelScope.launch(Dispatchers.IO) {
            listUsers = DataProvider.getListUsers()
        }
    }
    fun emailIsRegistered(mail: String): Boolean {
        for (user in listUsers) {
            if (mail == user.mail) {
                return true
            }
        }
        return false
    }
}