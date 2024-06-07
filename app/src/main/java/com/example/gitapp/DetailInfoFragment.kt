package com.example.gitapp

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class DetailInfoFragment : Fragment() {

    private lateinit var repositoryInfoViewModel: RepositoryInfoViewModel
    private lateinit var owner: String
    private lateinit var repo: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        owner = arguments?.getString("owner").orEmpty()
        repo = arguments?.getString("repo").orEmpty()

        val application = requireNotNull(this.activity).application
        repositoryInfoViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(RepositoryInfoViewModel::class.java)

        Log.d("DetailInfoFragment", "Loading repository details for $owner/$repo")
        repositoryInfoViewModel.loadRepositoryDetails(owner, repo)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_detail_info, container, false)

        val repoName = view.findViewById<TextView>(R.id.repository_name)
        val repoDescription = view.findViewById<TextView>(R.id.repository_description)
        val repoForks = view.findViewById<TextView>(R.id.repository_forks)
        val repoStars = view.findViewById<TextView>(R.id.repository_stars)
        val repoWatchers = view.findViewById<TextView>(R.id.repository_watchers)
        val repoLicense = view.findViewById<TextView>(R.id.repository_license)
        val repoReadme = view.findViewById<TextView>(R.id.repository_readme)
        val repoUrl = view.findViewById<TextView>(R.id.repository_url)

        repositoryInfoViewModel.repositoryDetails.observe(viewLifecycleOwner, { details ->
            details?.let {
                Log.d("DetailInfoFragment", "Repository details loaded: ${it.name}")
                repoName.text = it.name
                repoDescription.text = it.full_name
                repoForks.text = "Forks: ${it.forks}"
                repoStars.text = "Stars: ${it.stargazers_count}"
                repoWatchers.text = "Watchers: ${it.watchers_count}"
                repoLicense.text = "License: ${it.license?.name ?: "None"}"
                repoUrl.text = it.html_url

                // Получить Readme
                repositoryInfoViewModel.loadReadme(owner, repo)
            } ?: run {
                Log.d("DetailInfoFragment", "Repository details not found")
                Toast.makeText(context, "Repository details not found", Toast.LENGTH_SHORT).show()
            }
        })

        repositoryInfoViewModel.readme.observe(viewLifecycleOwner, { readme ->
            Log.d("DetailInfoFragment", "Readme loaded: ${readme?.content}")
            repoReadme.text = readme?.content ?: "No README found"
        })

        return view
    }

    companion object {
        fun newInstance(owner: String, repo: String): DetailInfoFragment {
            val fragment = DetailInfoFragment()
            val args = Bundle()
            args.putString("owner", owner)
            args.putString("repo", repo)
            fragment.arguments = args
            return fragment
        }
    }
}
