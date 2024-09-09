package com.example.intergrationv4

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

private lateinit var auth: FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        auth = FirebaseAuth.getInstance()

        val emailEditText = findViewById<EditText>(R.id.emailEditTextText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditTextText)
        val logInClick = findViewById<Button>(R.id.logInButton)
        val registerClick = findViewById<Button>(R.id.registerButton)

        //här sätter vi variablerna till email och password, vi ser till att ta bort mellanrum med trim() för att undvika oönskade uppgifter. Vi ser även till att
        //använderen skriver i något och meddelar användaren
        logInClick.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                signInUser(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            }
        }

        registerClick.setOnClickListener {
            val intent = Intent(this, Register::class.java)
            startActivity(intent)
        }


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    //här autentiserar vi om använderaren finns, gör den det så loggas den in och byter sida till Form/Credentials. Funkar det inte kommer det ett meddelande om varför
    //det inte funkade baserat på vad programmet ger för fel.
    private fun signInUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(this, "Welcome ${user?.email}", Toast.LENGTH_LONG).show()
                    val intent = Intent(this, Form::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
            .addOnFailureListener { e ->
                Log.d("Aleksander", "gicj inte att logga in:", e)
            }
    }
}