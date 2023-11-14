package com.adika.storyapp.data.local.repo

import com.adika.storyapp.data.local.pref.UserPreference
import com.adika.storyapp.data.remote.api.ApiService
import com.adika.storyapp.data.remote.response.GetAllStoryResponse
import com.adika.storyapp.data.remote.response.StoryDetailResponse

class StoryRepository private constructor(
    private val userPreference: UserPreference, private val apiService: ApiService
) {
    suspend fun getStory(): GetAllStoryResponse {
        return apiService.getStories()
    }

    suspend fun getDetailStory(id: String): StoryDetailResponse {
        return apiService.getDetailStory(id)
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            userPreference: UserPreference, apiService: ApiService
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(userPreference, apiService)
            }.also { instance = it }
    }
}