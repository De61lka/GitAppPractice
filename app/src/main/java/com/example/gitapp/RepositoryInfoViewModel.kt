package com.example.gitapp

import android.app.Application
import android.util.Base64
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import java.nio.charset.StandardCharsets

class RepositoryInfoViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: AppRepository = AppRepository(application)
    private val _repositoryDetails = MutableLiveData<RepositoryDetails>()
    val repositoryDetails: LiveData<RepositoryDetails> get() = _repositoryDetails

    private val _readme = MutableLiveData<Readme>()
    val readme: LiveData<Readme> get() = _readme

    fun loadRepositoryDetails(owner: String, repo: String) {
        viewModelScope.launch {
            try {
                Log.d("RepositoryInfoViewModel", "Loading repository details for $owner/$repo")
                val details = repository.getRepositoryDetails(owner, repo)
                if (details != null) {
                    Log.d("RepositoryInfoViewModel", "Repository details successfully loaded: ${details.toString()}")
                    _repositoryDetails.value = details
                } else {
                    Log.d("RepositoryInfoViewModel", "Repository details are null")
                }
            } catch (e: Exception) {
                Log.e("RepositoryInfoViewModel", "Error loading repository details", e)
            }
        }
    }

    fun decodeBase64(base64: String): String {
        val decodedBytes = Base64.decode(base64, Base64.DEFAULT)
        return String(decodedBytes, StandardCharsets.UTF_8)
    }

    fun loadReadme(owner: String, repo: String) {
        viewModelScope.launch {
            try {
                Log.d("RepositoryInfoViewModel", "Loading readme for $owner/$repo")
                val readme = repository.getReadme(owner, repo)
                if (readme != null) {
                    Log.d("RepositoryInfoViewModel", "Readme successfully loaded: ${readme.toString()}")
                    val decodedContent = decodeBase64(readme.content)
                    _readme.value = Readme(decodedContent)
                } else {
                    Log.d("RepositoryInfoViewModel", "Readme is null")
                }
            } catch (e: Exception) {
                Log.e("RepositoryInfoViewModel", "Error loading readme", e)
            }
        }
    }
}