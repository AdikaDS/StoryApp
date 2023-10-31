package com.adika.storyapp.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adika.storyapp.data.local.UserRepository
import com.adika.storyapp.data.local.pref.UserModel
import kotlinx.coroutines.launch

class LoginViewModel (private val repository: UserRepository) : ViewModel() {
    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}