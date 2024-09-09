package com.example.intergrationv4

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class game_API : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_api)

        val menuClick = findViewById<ImageButton>(R.id.menuButton)

        val url = "http://www.giantbomb.com/api/search/?api_key=946ec40e73ceccfef84795a86f54c0a65e67ad7a&format=json&query=\"metroid prime\"&resources=game"

        menuClick.setOnClickListener {
            val intent = Intent(this, Menu::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}