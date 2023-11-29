package com.adika.storyapp.data.local.repo

import com.adika.storyapp.data.remote.api.ApiService
import com.adika.storyapp.data.remote.response.GetAllStoryResponse

class MapsRepository (private val apiService: ApiService) {
    suspend fun getLocation(): GetAllStoryResponse {
        return apiService.getStoriesWithLocation()
    }

    companion object{
        @Volatile
        private var instance: MapsRepository? = null
        fun getInstance(apiService: ApiService):MapsRepository = instance ?: synchronized(this){
            instance ?: MapsRepository(apiService)
        }.also { instance = it }
    }
}