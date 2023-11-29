package com.adika.storyapp.di

import android.content.Context
import com.adika.storyapp.data.local.repo.UserRepository
import com.adika.storyapp.data.local.pref.UserPreference
import com.adika.storyapp.data.local.pref.dataStore
import com.adika.storyapp.data.local.repo.MapsRepository
import com.adika.storyapp.data.local.repo.StoryRepository
import com.adika.storyapp.data.remote.api.ApiConfig
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

object Injection {
    fun provideUserRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return UserRepository.getInstance(pref, apiService)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return StoryRepository.getInstance(pref, apiService)
    }

    fun provideMapsRepository(context: Context) : MapsRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        val user = runBlocking { pref.getSession().first() }
        val apiService = ApiConfig.getApiService(user.token)
        return MapsRepository.getInstance(apiService)
    }


}