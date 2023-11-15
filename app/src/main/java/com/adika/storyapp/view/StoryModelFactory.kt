package com.adika.storyapp.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adika.storyapp.data.local.repo.StoryRepository
import com.adika.storyapp.di.Injection
import com.adika.storyapp.view.addstory.AddStoryViewModel
import com.adika.storyapp.view.detail.DetailViewModel
import com.adika.storyapp.view.main.MainViewModel

class StoryModelFactory(private val repository: StoryRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }

            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(repository) as T
            }

            modelClass.isAssignableFrom(AddStoryViewModel::class.java) -> {
                AddStoryViewModel(repository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: StoryModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): StoryModelFactory {
            if (INSTANCE == null) {
                synchronized(StoryModelFactory::class.java) {
                    INSTANCE = StoryModelFactory(Injection.provideStoryRepository(context))
                }
            }
            return INSTANCE as StoryModelFactory
        }
    }
}