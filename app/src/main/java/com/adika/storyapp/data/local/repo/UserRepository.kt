package com.adika.storyapp.data.local.repo

import com.adika.storyapp.data.local.pref.UserModel
import com.adika.storyapp.data.local.pref.UserPreference
import com.adika.storyapp.data.remote.api.ApiService
import com.adika.storyapp.data.remote.response.LoginResponse
import com.adika.storyapp.data.remote.response.RegisterResponse
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val userPreference: UserPreference, private val apiService: ApiService
) {

    suspend fun login(email: String, password: String): LoginResponse {
        return apiService.login(email, password)
    }

    suspend fun register(name: String, email: String, password: String): RegisterResponse {
        return apiService.register(name, email, password)
    }

    suspend fun saveSession(user: UserModel) {
        userPreference.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreference.getSession()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreference, apiService: ApiService
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference, apiService)
            }.also { instance = it }
    }
}