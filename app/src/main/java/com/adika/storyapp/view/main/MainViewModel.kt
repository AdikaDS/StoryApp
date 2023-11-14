package com.adika.storyapp.view.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adika.storyapp.data.local.repo.StoryRepository
import com.adika.storyapp.data.remote.response.ListStoryItem
import kotlinx.coroutines.launch

class MainViewModel(private val repository: StoryRepository) : ViewModel() {

    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory: LiveData<List<ListStoryItem>> = _listStory

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    init {
        getStory()
    }

    fun getStory() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val response = repository.getStory()
                if (!response.error) {
                    _listStory.value = response.listStory
                } else {

                    Log.e("StoryViewModel", "Error fetching stories: ${response.message}")
                }
            } catch (e: Exception) {
                Log.e("StoryViewModel", "Error fetching stories: ${e.message}")
            } finally {
                _loading.value = false
            }
        }
    }
}
