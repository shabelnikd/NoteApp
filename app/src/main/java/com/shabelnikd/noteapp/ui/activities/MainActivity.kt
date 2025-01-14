package com.shabelnikd.noteapp.ui.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.google.firebase.auth.FirebaseAuth
import com.shabelnikd.noteapp.R
import com.shabelnikd.noteapp.databinding.ActivityMainBinding
import com.shabelnikd.noteapp.datastore.DataStoreManager
import com.shabelnikd.noteapp.ui.activities.auth.FirebaseUIActivity
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private var _binding: ActivityMainBinding? = null
    private val binding get() = _binding!!

//    private var _dataStore: DataStoreManager? = null
//    private val dataStoreManager get() = _dataStore!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        _binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragment_container) as NavHostFragment

        val navController = navHostFragment.navController

        lifecycleScope.launch {
            val isFirstLaunch = DataStoreManager.isFirstLaunch()
            val isAuthenticated = FirebaseAuth.getInstance().currentUser

            if (isFirstLaunch) {
                navController.navigate(R.id.onBoardFragment)

            } else if (isAuthenticated != null) {
                navController.navigate(R.id.homeFragment)
            } else {
                val intent = Intent(this@MainActivity, FirebaseUIActivity::class.java)
                startActivity(intent)
                finish()
            }

        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}