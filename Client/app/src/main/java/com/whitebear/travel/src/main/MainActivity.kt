package com.whitebear.travel.src.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.whitebear.travel.R
import com.whitebear.travel.config.BaseActivity
import com.whitebear.travel.databinding.ActivityMainBinding
import com.whitebear.travel.src.network.viewmodel.MainViewModel

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate){
    lateinit var mainViewModel : MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initNavigation()
        setInstance()
    }
    private fun setInstance(){
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private fun initNavigation(){
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.activity_main_navHost) as NavHostFragment

        // 네비게이션 컨트롤러
        val navController = navHostFragment.navController

        // 바인딩
        NavigationUI.setupWithNavController(binding.activityMainBottomNav, navController)
    }


    /**
     * bottom Nav hide & show
     * hide - true
     * show - false
     */
    fun hideBottomNav(state: Boolean) {
        if(state) {
            binding.activityMainBottomNav.visibility = View.GONE
        } else {
            binding.activityMainBottomNav.visibility = View.VISIBLE
        }
    }
}