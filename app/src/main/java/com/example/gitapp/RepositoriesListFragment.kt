package com.example.gitapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class RepositoriesListFragment : Fragment() {

    private lateinit var repositoriesListViewModel: RepositoriesListViewModel
    private lateinit var adapter: ReposAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_repositories_list, container, false)
        val application = requireNotNull(this.activity).application
        repositoriesListViewModel = ViewModelProvider(
            this,
            ViewModelProvider.AndroidViewModelFactory.getInstance(application)
        ).get(RepositoriesListViewModel::class.java)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ReposAdapter(mutableListOf())
        recyclerView.adapter = adapter

        repositoriesListViewModel.repositories.observe(viewLifecycleOwner, { repositories ->
            if (repositories != null) {
                adapter.updateRepos(repositories)
            } else {
                Toast.makeText(context, "No repositories found", Toast.LENGTH_SHORT).show()
            }
        })

        val token = arguments?.getString("token")
        token?.let {
            fetchRepos(it)
        } ?: run {
            Toast.makeText(context, "Token not found", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun fetchRepos(token: String) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://api.github.com/user/repos?per_page=10&sort=updated&direction=desc")
            .header("Authorization", "Bearer $token")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    Toast.makeText(context, "Failed to fetch repos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val jsonResponse = response.body?.string()
                    val repos = Gson().fromJson(jsonResponse, Array<Repository>::class.java).toList()
                    activity?.runOnUiThread {
                        updateRepoList(repos)
                    }
                } else {
                    activity?.runOnUiThread {
                        Toast.makeText(context, "No repos found", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun updateRepoList(repos: List<Repository>) {
        if (repos.isNotEmpty()) {
            adapter.updateRepos(repos)
            adapter.notifyDataSetChanged()
        } else {
            Toast.makeText(context, "No repos found", Toast.LENGTH_SHORT).show()
        }
    }
}
