package com.example.intergrationv4

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        val menuClick = findViewById<ImageButton>(R.id.menuButton)
        val credentialsMenuClick = findViewById<Button>(R.id.formMenuButton)
        val registerMenuClick = findViewById<Button>(R.id.registerMenuButton)
        val logOutMenuClick = findViewById<Button>(R.id.logOutMenuButton)

        //En meny med knappar för att byta till andra sidor

        menuClick.setOnClickListener {
            finish()
        }

        credentialsMenuClick.setOnClickListener {
            val intent = Intent(this, Form::class.java)
            startActivity(intent)
        }

        registerMenuClick.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }

        logOutMenuClick.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}