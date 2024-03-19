package com.example.arventurepath.ui.login_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arventurepath.data.models.UserToRegister
import com.example.arventurepath.data.remote.DataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt

class LoginViewModel: ViewModel() {

    private var listUsers: List<UserToRegister> = listOf()
    private val _idUser = MutableStateFlow(-1)
    val idUser: StateFlow<Int> = _idUser

    fun getListUsers(){
        viewModelScope.launch(Dispatchers.IO) {
            listUsers = DataProvider.getListUsers()
        }
    }
    fun isEmailRegistered(mail: String): Boolean {
        for (user in listUsers) {
            if (mail == user.mail) {
                return true
            }
        }
        return false
    }

    fun hashPassword(password: String): String {
       return BCrypt.hashpw(password, BCrypt.gensalt())
    }

    fun userExist(mail: String, password: String): Int{
        for (user in listUsers) {
            if (mail == user.mail && BCrypt.checkpw(password,user.passwd)) {
                return user.id
            }
        }
        return -1
    }
    fun registerUser(mail: String, hashPass: String){
        val userRegister = UserToRegister(mail = mail, passwd = hashPass)
        viewModelScope.launch(Dispatchers.IO) {
            val valor = DataProvider.registerUser(userRegister)
            _idUser.emit(valor.id)
        }
    }
}
