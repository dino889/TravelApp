package com.whitebear.travel.src.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.whitebear.travel.R
import com.whitebear.travel.config.BaseActivity
import com.whitebear.travel.databinding.ActivityMainBinding

class MainActivity : BaseActivity<ActivityMainBinding>(ActivityMainBinding::inflate){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initNavigation()
    }
    fun initNavigation(){

    }
}