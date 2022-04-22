package com.example.sankalan.activities

import android.content.Intent
import android.os.Bundle

import android.view.Menu
import android.view.MenuItem
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import com.example.sankalan.MainViewModel
import com.example.sankalan.R
import com.example.sankalan.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    //ViewModel
    lateinit var mainViewModel:MainViewModel

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    lateinit var bottom:BottomNavigationView
//    lateinit var viewModelData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)
        // Bottom Navigation
        val navHost = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        val navControl = navHost.navController
        bottom = findViewById(R.id.bottom_navigation)
        bottom.setupWithNavController(navControl)
        //drawer layout
         appBarConfiguration = AppBarConfiguration(navControl.graph,binding.drawerLayout)
        setupActionBarWithNavController(navControl,binding.drawerLayout)
        binding.navView.setupWithNavController(navControl)
    //View model
    mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.action_logout){
            Thread(Runnable {
                mainViewModel.logout()

            })
            startActivity(Intent(this,LoginActivity::class.java))
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

    private fun  getUserData(){

    }

}