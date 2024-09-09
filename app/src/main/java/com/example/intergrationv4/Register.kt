package com.example.intergrationv4


import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore

private lateinit var auth: FirebaseAuth

class Register : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)

        auth = FirebaseAuth.getInstance()

        val emailEditText = findViewById<EditText>(R.id.emailRegisterEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordRegisterEditText)
        val createUserButton = findViewById<Button>(R.id.createUserButton)
        val backToLogin = findViewById<Button>(R.id.backToLogInButton)

        backToLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        //en knapp som kallar på funktionen createUser om email och lösenord är i fyllt, om det inte är i fyllt så kommer det ett meddelande som ber användaren att fylla i
        createUserButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                createUser(email, password)
            } else {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_LONG).show()
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    //skapar en användare med hjälp av firebase authenticator och lägger in användaren i firebase firestore databasen. Skapar en map och använder nycklar
    private fun createUser(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    val db = Firebase.firestore
                    val user = hashMapOf(
                        "email" to email
                    )

                    if (userId != null) {
                        db.collection("users").document(userId)
                            .set(user)
                            .addOnSuccessListener {
                                Log.d("Aleksander", "dokumentet skapades med id: $userId")
                            }
                            .addOnFailureListener { e ->
                                Log.d("Aleksander", "error att addera", e)
                            }
                    }

                    Toast.makeText(this, "User created successfully", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(
                        this,
                        "Authentication failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

    }
}