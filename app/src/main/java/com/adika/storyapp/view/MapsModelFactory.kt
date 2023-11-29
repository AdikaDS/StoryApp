package com.adika.storyapp.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adika.storyapp.data.local.repo.MapsRepository
import com.adika.storyapp.di.Injection
import com.adika.storyapp.view.maps.MapsViewModel

class MapsModelFactory(private val repository: MapsRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(repository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: MapsModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): MapsModelFactory {
            if (INSTANCE == null) {
                synchronized(MapsModelFactory::class.java) {
                    INSTANCE = MapsModelFactory(Injection.provideMapsRepository(context))
                }
            }
            return INSTANCE as MapsModelFactory
        }
    }
}
