package com.adika.storyapp.di

import android.content.Context
import com.adika.storyapp.data.local.UserRepository
import com.adika.storyapp.data.local.pref.UserPreference
import com.adika.storyapp.data.local.pref.dataStore

object Injection {
    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }

}