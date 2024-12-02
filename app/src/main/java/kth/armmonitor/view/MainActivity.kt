package kth.armmonitor.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kth.armmonitor.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set an empty content view or simply skip setting it since we are using fragments
        if (savedInstanceState == null) {
            // Adding the MonitorFragment to the activity
            supportFragmentManager.beginTransaction()
                .replace(android.R.id.content, Screen())  // Use default root view ID
                .commit()
        }
    }
}