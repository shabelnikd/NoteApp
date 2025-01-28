package com.shabelnikd.noteapp.ui.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.shabelnikd.noteapp.R
import com.shabelnikd.noteapp.databinding.ActivityMainBinding
import com.shabelnikd.noteapp.utils.PreferenceHelper
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val sharedPreferences = PreferenceHelper()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        sharedPreferences.initialize(this)

        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment
        val inflater = navHostFragment.navController.navInflater
        val navGraph = inflater.inflate(R.navigation.nav_graph)

        if (!sharedPreferences.isFirstLaunch && FirebaseAuth.getInstance().currentUser == null) {
            navGraph.setStartDestination(R.id.authFragment)
        } else if (!sharedPreferences.isFirstLaunch && FirebaseAuth.getInstance().currentUser != null) {
            navGraph.setStartDestination(R.id.homeFragment)
        }

        navHostFragment.navController.graph = navGraph

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}