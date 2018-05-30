package com.demo.slice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.demo.slice.domain.ext.newInstance
import com.demo.slice.ui.main.MainFragment

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
