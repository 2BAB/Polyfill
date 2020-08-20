package me.xx2bab.polyfill.sample

import android.os.Bundle
import me.xx2bab.gradle.scratchpaper.sample.R
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

}

