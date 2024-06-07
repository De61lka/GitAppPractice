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

    suspend fun getRepositories(): List<Repository>? {
        return try {
            val repos = api?.getRepositories()
            Log.d("AppRepository", "Repositories fetched: ${repos?.size}")
            repos
        } catch (e: Exception) {
            Log.e("AppRepository", "Error fetching repositories", e)
            null
        }
    }

    suspend fun getRepositoryDetails(owner: String, repo: String): RepositoryDetails? {
        return try {
            val details = api?.getRepositoryDetails(owner, repo)
            Log.d("AppRepository", "Repository details response: ${details.toString()}")
            details
        } catch (e: Exception) {
            Log.e("AppRepository", "Error fetching repository details", e)
            null
        }
    }

    suspend fun getReadme(owner: String, repo: String): Readme? {
        return try {
            val readme = api?.getReadme(owner, repo)
            Log.d("AppRepository", "Readme response: ${readme.toString()}")
            readme
        } catch (e: Exception) {
            Log.e("AppRepository", "Error fetching readme", e)
            null
        }
    }
}
