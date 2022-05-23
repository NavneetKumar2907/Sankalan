package com.example.sankalan.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.HandlerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.sankalan.model.MainViewModel
import com.example.sankalan.R
import com.example.sankalan.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

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
        setupActionBarWithNavController(navControl, binding.drawerLayout)
        binding.navView.setupWithNavController(navControl)

        //Initialising view Model
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        //Running Executor for verification
        Firebase.auth.addAuthStateListener {
            Log.w("IS","${it.currentUser?.isEmailVerified}")
        }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.action_logout) {

                mainViewModel.logout()

            startActivity(Intent(this, LoginActivity::class.java))
            this.finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }


}