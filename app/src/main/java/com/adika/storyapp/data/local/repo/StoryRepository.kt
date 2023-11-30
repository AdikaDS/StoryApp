package com.adika.storyapp.data.local.repo

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.adika.storyapp.data.local.pref.UserPreference
import com.adika.storyapp.data.paging.StoryPagingSource
import com.adika.storyapp.data.paging.StoryRemoteMediator
import com.adika.storyapp.data.remote.api.ApiService
import com.adika.storyapp.data.remote.response.AddStoryResponse
import com.adika.storyapp.data.remote.response.ListStoryItem
import com.adika.storyapp.data.remote.response.StoryDetailResponse
import com.adika.storyapp.database.StoryDatabase
import com.google.gson.Gson
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class StoryRepository private constructor(
    private val storyDatabase: StoryDatabase,
    private val userPreference: UserPreference,
    private val apiService: ApiService
) {
//    suspend fun getStory(): GetAllStoryResponse {
//        return apiService.getStories()
//    }

    fun getStory(): LiveData<PagingData<ListStoryItem>> {
        @OptIn(ExperimentalPagingApi::class)
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService),
            pagingSourceFactory = {
                StoryPagingSource(apiService)
                storyDatabase.storyDao().getAllStory()
            }
        ).liveData
    }

    suspend fun getDetailStory(id: String): StoryDetailResponse {
        return apiService.getDetailStory(id)
    }

    fun uploadStory(imageFile: File, description: String) {
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        Log.d("TAG", "uploadStory: $description")
        GlobalScope.launch {
            try {
                val successResponse = apiService.uploadImage(multipartBody, requestBody).execute()
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, AddStoryResponse::class.java)
                Log.d("UploadError", "Error uploading story: ${errorResponse?.message}")
            }
        }

    }

    fun uploadStoryWithLocation(imageFile: File, description: String, lat: Double, lon: Double) {
        val requestBody = description.toRequestBody("text/plain".toMediaType())
        val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
        val multipartBody = MultipartBody.Part.createFormData(
            "photo",
            imageFile.name,
            requestImageFile
        )
        Log.d("TAG", "uploadStory: $description")
        GlobalScope.launch {
            try {
                val successResponse =
                    apiService.uploadImageWithLocaation(multipartBody, requestBody, lat, lon)
                        .execute()
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, AddStoryResponse::class.java)
                Log.d("UploadError", "Error uploading story: ${errorResponse?.message}")
            }
        }

    }


    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            storyDatabase: StoryDatabase,
            userPreference: UserPreference,
            apiService: ApiService
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(storyDatabase, userPreference, apiService)
            }.also { instance = it }
    }
}