package com.shabelnikd.noteapp.ui.activities

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.motion.widget.OnSwipe
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.navigation.NavigationView
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
        binding.navView.setupWithNavController(navHostFragment.navController)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime());
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom + imeInsets.bottom)
            insets
        }

    }

    fun openDrawer() {
        val drawerLayout: DrawerLayout = binding.root
        drawerLayout.openDrawer(GravityCompat.START)
    }
}