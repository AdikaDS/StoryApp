package com.adika.storyapp.view

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.adika.storyapp.data.local.repo.UserRepository
import com.adika.storyapp.di.Injection
import com.adika.storyapp.view.login.LoginViewModel
import com.adika.storyapp.view.signup.SignupViewModel
import com.adika.storyapp.view.welcome.WelcomeViewModel

class UserModelFactory(private val repository: UserRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(WelcomeViewModel::class.java) -> {
                WelcomeViewModel(repository) as T
            }

            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }

            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(repository) as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): UserModelFactory {
            if (INSTANCE == null) {
                synchronized(UserModelFactory::class.java) {
                    INSTANCE = UserModelFactory(Injection.provideUserRepository(context))
                }
            }
            return INSTANCE as UserModelFactory
        }
    }
}