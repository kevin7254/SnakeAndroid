package com.example.snakegame

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

/**
 * The Home Screen activity.
 */
class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        findViewById<View>(R.id.btnPlayGame).setOnClickListener {
            val gameIntent = Intent(this, MainActivity::class.java)
            startActivity(gameIntent)
        }

        findViewById<View>(R.id.btnStats).setOnClickListener {
            // val statsIntent = Intent(this, StatsActivity::class.java)
            // startActivity(statsIntent)
        }

        findViewById<View>(R.id.btnSettings).setOnClickListener {
            // val settingsIntent = Intent(this, SettingsActivity::class.java)
            //  startActivity(settingsIntent)
        }
    }
}