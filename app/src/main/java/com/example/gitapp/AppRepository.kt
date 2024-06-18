package com.example.gitapp

import android.content.Context
import android.util.Log

class AppRepository(context: Context) {
    private val keyValueStorage: KeyValueStorage = KeyValueStorage(context)
    private var api: GitHubApi? = null

    fun saveAuthToken(token: String) {
        keyValueStorage.saveData("auth_token", token)
        api = RetrofitClient.getClient(token).create(GitHubApi::class.java)
        Log.d("AppRepository", "API client initialized with token")
    }

    fun getAuthToken(): String? {
        return keyValueStorage.getData("auth_token")
    }

    private fun getApi(): GitHubApi? {
        if (api == null) {
            val token = getAuthToken()
            if (token != null) {
                api = RetrofitClient.getClient(token).create(GitHubApi::class.java)
            }
        }
        return api
    }

    suspend fun getRepositories(): List<Repository>? {
        return try {
            val repos = getApi()?.getRepositories()
            if (repos != null) {
                Log.d("AppRepository", "Repositories fetched: ${repos.size}")
            } else {
                Log.d("AppRepository", "Repositories fetched: null")
            }
            repos
        } catch (e: Exception) {
            Log.e("AppRepository", "Error fetching repositories", e)
            null
        }
    }

    suspend fun getRepositoryDetails(owner: String, repo: String): RepositoryDetails? {
        return try {
            val url = "https://api.github.com/repos/$owner/$repo"
            Log.d("AppRepository", "Fetching repository details from URL: $url")
            val response = getApi()?.getRepositoryDetails(owner, repo)
            if (response != null) {
                Log.d("AppRepository", "Repository details response: ${response.toString()}")
            } else {
                Log.d("AppRepository", "Repository details response is null")
            }
            response
        } catch (e: Exception) {
            Log.e("AppRepository", "Error fetching repository details", e)
            null
        }
    }

    suspend fun getReadme(owner: String, repo: String): Readme? {
        return try {
            val readme = getApi()?.getReadme(owner, repo)
            Log.d("AppRepository", "Readme response: ${readme.toString()}")
            readme
        } catch (e: Exception) {
            Log.e("AppRepository", "Error fetching readme", e)
            null
        }
    }
}
