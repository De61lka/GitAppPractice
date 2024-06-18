package com.example.gitapp

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AppRepository = AppRepository(application)

    private val _authenticationState = MutableLiveData<Boolean>()
    val authenticationState: LiveData<Boolean> get() = _authenticationState

    fun authenticate(token: String) {
        viewModelScope.launch {
            repository.saveAuthToken(token)
            _authenticationState.value = true
        }
    }

    fun getAuthToken(): String? {
        return repository.getAuthToken()
    }

    fun loadRepositories() {
        viewModelScope.launch {
            repository.getRepositories()
        }
    }
}
