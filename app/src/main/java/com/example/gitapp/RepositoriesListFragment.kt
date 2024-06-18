package com.example.gitapp

import android.os.Bundle
import android.util.Log
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
            Log.d("RepositoriesListFragment", "Repositories observed: $repositories")
            if (repositories != null && repositories.isNotEmpty()) {
                Log.d("RepositoriesListFragment", "Repositories loaded: ${repositories.size}")
                adapter.updateRepos(repositories)
            } else {
                Log.d("RepositoriesListFragment", "No repositories found")
                Toast.makeText(context, "No repositories found", Toast.LENGTH_SHORT).show()
            }
        })

        val token = arguments?.getString("token")
        token?.let {
            repositoriesListViewModel.fetchRepositories()
        } ?: run {
            Toast.makeText(context, "Token not found", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}
