package com.bug.incrementalkapt

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    private lateinit var activityComponent: ActivityComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        activityComponent = DaggerActivityComponent.create()
    }
}