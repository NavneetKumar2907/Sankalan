package com.example.sankalan.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.sankalan.R
import com.example.sankalan.databinding.ActivityMainBinding
import com.example.sankalan.model.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    //ViewModel
    lateinit var mainViewModel: MainViewModel

    private lateinit var appBarConfiguration: AppBarConfiguration //App Bar
    private lateinit var binding: ActivityMainBinding
    lateinit var bottom: BottomNavigationView //Bottom Navigation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
        //setting Up UI
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

        // Bottom Navigation
        val navHost =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navControl = navHost.navController

        bottom = findViewById(R.id.bottom_navigation)
        bottom.setupWithNavController(navControl)

        //drawer layout
        appBarConfiguration = AppBarConfiguration(navControl.graph, binding.drawerLayout)

        //Initialising view Model
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        //Running Executor for verification
        Firebase.auth.addAuthStateListener {
            Log.w("IS", "${it.currentUser?.isEmailVerified}")
        }

        binding.navView.setNavigationItemSelectedListener { item ->

            when(item.itemId){
                R.id.logout-> {
                    log()
                }
                else -> {
                    false
                }
            }
        }
        setupActionBarWithNavController(navControl, binding.drawerLayout)
        binding.navView.setupWithNavController(navControl)

    }

    fun log(): Boolean {
        mainViewModel.logout()
        startActivity(Intent(this, LoginActivity::class.java))
        this.finish()
        return true
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }


}