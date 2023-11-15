package com.adika.storyapp.view.addstory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adika.storyapp.data.local.repo.StoryRepository
import com.adika.storyapp.data.remote.response.AddStoryResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.File

class AddStoryViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    val error = MutableLiveData<String?>()
    val status: MutableLiveData<Boolean> = MutableLiveData()
    fun uploadImage(file: File, description: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val response = repository.uploadStory(file, description)
                status.postValue(true)
                Log.d("AddStory", "$response")
            } catch (e: HttpException) {
                _loading.value = false
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, AddStoryResponse::class.java)
                error.postValue(errorBody.message)
                status.postValue(false)
                Log.d("AddStory", "$e")
            } catch (e: Exception) {
                _loading.value = true
                error.postValue("Terjadi kesalahan saat membuat data")
                status.postValue(false)
                Log.d("AddStory", "$e")
            }
        }
    }

}