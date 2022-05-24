package com.sankalan.activities

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
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
import com.sankalan.R
import com.sankalan.databinding.ActivityMainBinding
import com.sankalan.model.MainViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
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

        setupActionBarWithNavController(navControl, binding.drawerLayout)
        binding.navView.setupWithNavController(navControl)

        bottom = findViewById(R.id.bottom_navigation)
        bottom.setupWithNavController(navControl)

        //drawer layout
        appBarConfiguration = AppBarConfiguration(navControl.graph, binding.drawerLayout)

        //Initialising view Model
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)


        binding.navView.menu.findItem(R.id.logout).setOnMenuItemClickListener {
            if(it.itemId == R.id.logout){
                log()
                true
            }else{
                false
            }
        }



    }

    fun log() {
        mainViewModel.logout()
        startActivity(Intent(this, LoginActivity::class.java))
        this.finish()

    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId==R.id.time_table){
            //Fetch time table
            try{
                Log.w("Value: ",mainViewModel.liveTimeTable.value.toString())

                val uri: Uri =
                    Uri.parse(mainViewModel.liveTimeTable.value)

                Executors.newSingleThreadExecutor().execute {
                    val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

                    val request = DownloadManager.Request(uri)
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                    val reference: Long = manager.enqueue(request)
                }

                return true
            }catch (e:Exception){
                Log.w("Error: ",e.message.toString())
            }
        }
        return super.onOptionsItemSelected(item)
    }


}