package com.example.gitapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider

class AuthFragment : Fragment() {

    private lateinit var authViewModel: AuthViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_auth, container, false)
        val application = requireNotNull(this.activity).application
        authViewModel = ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory.getInstance(application)).get(AuthViewModel::class.java)

        val tokenInput = view.findViewById<EditText>(R.id.token_input)
        val loginButton = view.findViewById<Button>(R.id.login_button)

        loginButton.setOnClickListener {
            val token = tokenInput.text.toString()
            authViewModel.authenticate(token)
        }

        authViewModel.authenticationState.observe(viewLifecycleOwner, { isAuthenticated ->
            if (isAuthenticated) {
                // Перейти к списку репозиториев
                val fragment = RepositoriesListFragment()
                val args = Bundle()
                args.putString("token", authViewModel.getAuthToken())
                fragment.arguments = args
                requireActivity().supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit()

                // Загрузить репозитории
                authViewModel.loadRepositories()
            } else {
                Toast.makeText(context, "Authentication failed", Toast.LENGTH_SHORT).show()
            }
        })



        return view
    }
}
