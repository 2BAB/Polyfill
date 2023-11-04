package me.xx2bab.polyfill.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import me.xx2bab.polyfill.sample.android.ExportedAndroidLibraryRunnable

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        ExportedAndroidLibraryRunnable().run()
    }

}

