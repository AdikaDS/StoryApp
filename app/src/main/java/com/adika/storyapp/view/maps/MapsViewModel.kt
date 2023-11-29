package com.adika.storyapp.view.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.adika.storyapp.data.local.repo.MapsRepository
import com.adika.storyapp.data.remote.response.GetAllStoryResponse
import kotlinx.coroutines.launch

class MapsViewModel(private val repository: MapsRepository) : ViewModel() {

    private val _maps = MutableLiveData<GetAllStoryResponse>()
    val maps: LiveData<GetAllStoryResponse> = _maps

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun getLocation() {
        viewModelScope.launch {
            try {
                _loading.value = true
                val response = repository.getLocation()
                val location = response.listStory.firstOrNull()
                _maps.value = response
                Log.e("MapsViewModel", "Lat : ${location?.lat}, Lon : ${location?.lon}")
            } catch (e: Exception) {
                Log.e("MapsViewModel", "$e")
            } finally {
                _loading.value = false
            }
        }
    }
}
