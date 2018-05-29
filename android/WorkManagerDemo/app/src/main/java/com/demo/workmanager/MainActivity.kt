package com.demo.workmanager

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.demo.workmanager.ext.newInstance
import com.demo.workmanager.ui.main.MainFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        savedInstanceState?.run {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment::class.newInstance(applicationContext))
                .commitNow()
        }
    }
}
