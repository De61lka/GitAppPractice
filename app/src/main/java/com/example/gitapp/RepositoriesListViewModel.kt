package com.example.gitapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RepositoriesListViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AppRepository = AppRepository(application)
    private val _repositories = MutableLiveData<List<Repository>>()
    val repositories: LiveData<List<Repository>> get() = _repositories

    init {
        viewModelScope.launch {
            val repos = repository.getRepositories()
            if (repos != null) {
                _repositories.value = repos
            } else {
                _repositories.value = emptyList()
            }
        }
    }
}
