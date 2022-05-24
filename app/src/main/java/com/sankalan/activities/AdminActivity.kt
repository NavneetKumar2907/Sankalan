package com.sankalan.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.sankalan.R
import com.sankalan.databinding.ActivityAdminBinding
import com.sankalan.model.AdminViewModel

/**
 * Activity For Admin Login and Features.
 */

lateinit var binding: ActivityAdminBinding // Root UI
lateinit var adminViewModel: AdminViewModel // Admin View Model

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //For Full Screen
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()

        //Intializing binding
        binding = ActivityAdminBinding.inflate(layoutInflater)
        //Initializing ViewModel
        adminViewModel = ViewModelProvider(this).get(AdminViewModel::class.java)
        setContentView(binding.root)//SetUp View
        setSupportActionBar(binding.adminToolbar)

        binding.adminToolbar.title = "Admin" // Admin ToolBar Title
        try {
            binding.adminToolbar.setTitleTextColor(getColor(R.color.white)) //TextColor

        }catch (e:Exception){
            Log.w("Error: ",e.message.toString())
        }
        //NavHost
        val navHost =
            supportFragmentManager.findFragmentById(R.id.admin_fragment_container) as NavHostFragment
        //Navigation Controller
        val navControl = navHost.navController
        //Setting Up Bottom Navigation
        binding.adminBottom.setupWithNavController(navControl)
    }

    //Menu
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu
        // this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.admin_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.action_logout) {

             adminViewModel.logout()
            startActivity(Intent(this, LoginActivity::class.java))
            this.finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

}