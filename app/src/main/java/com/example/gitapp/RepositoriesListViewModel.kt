package com.example.gitapp

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class RepositoriesListViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AppRepository = AppRepository(application)
    private val _repositories = MutableLiveData<List<Repository>>()
    val repositories: LiveData<List<Repository>> get() = _repositories

    fun fetchRepositories() {
        viewModelScope.launch {
            try {
                val repos = repository.getRepositories()
                Log.d("RepositoriesListViewModel", "Repositories fetched: $repos")
                if (repos != null) {
                    Log.d("RepositoriesListViewModel", "Repositories successfully fetched: ${repos.size}")
                    _repositories.value = repos
                } else {
                    Log.d("RepositoriesListViewModel", "No repositories fetched")
                    _repositories.value = emptyList()
                }
            } catch (e: Exception) {
                Log.e("RepositoriesListViewModel", "Error fetching repositories", e)
                _repositories.value = emptyList()
            }
        }
    }
}
