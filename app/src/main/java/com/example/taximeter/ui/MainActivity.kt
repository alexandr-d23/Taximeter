package com.example.taximeter.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.taximeter.R
import com.example.taximeter.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onStart() {
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportFragmentManager.beginTransaction().replace(
            R.id.fl_container,
            TimerFragment.newInstance()
        ).commit()
        binding.bnvMain.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.item_timer -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.fl_container,
                        TimerFragment.newInstance()
                    ).commit()
                    true
                }
                R.id.item_history -> {
                    supportFragmentManager.beginTransaction().replace(
                        R.id.fl_container,
                        HistoryFragment()
                    ).commit()
                    true
                }
                else -> false
            }
        }
        binding.bnvMain.setOnNavigationItemReselectedListener {

        }
    }

}