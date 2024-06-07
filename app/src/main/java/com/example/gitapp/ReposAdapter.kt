package com.example.gitapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class ReposAdapter(private val repos: MutableList<Repository>) : RecyclerView.Adapter<ReposAdapter.RepoViewHolder>() {

    class RepoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameTextView: TextView = view.findViewById(R.id.repoName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.repo_item, parent, false)
        return RepoViewHolder(view)
    }

    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        val repo = repos[position]
        holder.nameTextView.text = repo.name
        holder.itemView.setOnClickListener {
            val fragment = DetailInfoFragment.newInstance(repo.owner.login, repo.name)
            (holder.itemView.context as AppCompatActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun getItemCount() = repos.size

    fun updateRepos(newRepos: List<Repository>) {
        repos.clear()
        repos.addAll(newRepos)
        notifyDataSetChanged()
    }
}
