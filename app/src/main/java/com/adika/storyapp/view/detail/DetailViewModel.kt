package com.adika.storyapp.view.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adika.storyapp.data.local.repo.StoryRepository
import com.adika.storyapp.data.remote.response.Story
import kotlinx.coroutines.launch

class DetailViewModel(private val repository: StoryRepository) : ViewModel() {
    val error = MutableLiveData<String?>()

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    val detailStory = MutableLiveData<Story>()

    val status: MutableLiveData<Boolean> = MutableLiveData()

    fun getDetailStory(id: String) {
        viewModelScope.launch {
            try {
                _loading.value = true
                val detailResponse = repository.getDetailStory(id)
                if (!detailResponse.error) {
                    detailStory.value = detailResponse.story
                } else {
                    Log.e("DetailViewModel", "Error load stories: ${detailResponse.message}")
                }
            } catch (e: Exception) {
                Log.e("DetailViewModel", "Error load stories: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }
}