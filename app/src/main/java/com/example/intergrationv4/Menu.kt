package com.example.intergrationv4

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.intergrationv4.MainActivity.Companion.navController
import com.google.firebase.auth.FirebaseAuth

private lateinit var auth: FirebaseAuth

class Menu : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu)

        val menuClick = findViewById<ImageButton>(R.id.menuButton)
        val credentialsMenuClick = findViewById<Button>(R.id.formMenuButton)
        val registerMenuClick = findViewById<Button>(R.id.registerMenuButton)
        val gameAPIMenuClick = findViewById<Button>(R.id.gameAPIMenuButton)
        val movieAPIMenuClick = findViewById<Button>(R.id.movieAPIMenuButton)
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

        gameAPIMenuClick.setOnClickListener {
            val intent = Intent(this, game_API::class.java)
            startActivity(intent)
        }

        movieAPIMenuClick.setOnClickListener {
            val intent = Intent(this, TMDB_API::class.java)
            startActivity(intent)
        }

        //Här använder vi navController och popUpTo för att rensa stacken så en användare inte kan logga ut och ta sig in igen
        logOutMenuClick.setOnClickListener {
            auth.signOut()
            navController.navigate(R.id.mainActivity) {
                popUpTo(R.id.menu) { inclusive = true }
        //ett annat sätt att logga ut och ta bort backstack hade varit med intent:
//        val intent = Intent(this, MainActivity::class.java)
//        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//        startActivity(intent)
//        finish()
            }
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}