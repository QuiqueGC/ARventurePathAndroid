package com.example.arventurepath.ui.login_fragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.arventurepath.data.models.UserToRegister
import com.example.arventurepath.data.remote.DataProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.mindrot.jbcrypt.BCrypt

class LoginViewModel: ViewModel() {

    private var listUsers: List<UserToRegister> = listOf()

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
    fun comparePasswords(hashedPassword1: String, hashedPassword2: String): Boolean {
        return BCrypt.checkpw(hashedPassword1, hashedPassword2)
    }
    fun userExist(mail: String, hashpasswd: String): Int{
        for (user in listUsers) {
            if (mail == user.mail && comparePasswords(hashpasswd,user.passwd)) {
                return user.id
            }
        }
        return -1
    }

}
