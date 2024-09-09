package com.example.intergrationv4

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Form : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_formular)

        auth = FirebaseAuth.getInstance()
        db = FirebaseFirestore.getInstance()

        val nameEditText = findViewById<EditText>(R.id.NameEditText)
        val lastNameEditText = findViewById<EditText>(R.id.lastNameEditText)
        val emailTextView = findViewById<TextView>(R.id.emailTextView)
        val phoneEditText = findViewById<EditText>(R.id.editTextPhone)
        val saveButton = findViewById<Button>(R.id.saveCredentialsButton)
        val menuClick = findViewById<ImageButton>(R.id.menuButton)

        //hämtar datan från databasen (specifikt för user) och tar en callback som argument
        loadUserCredentials { user ->
            nameEditText.setText(user["firstName"] as String?)
            lastNameEditText.setText(user["lastName"] as String?)
            emailTextView.text = user["email"] as String?
            phoneEditText.setText(user["phone"] as String?)
        }

        //när spar-knappen trycks så uppdateras datan till det man har skrivit i appen in i databasen
        saveButton.setOnClickListener {
            val updatedUser = hashMapOf(
                "firstName" to nameEditText.text.toString(),
                "lastName" to lastNameEditText.text.toString(),
                "email" to emailTextView.text.toString(),
                "phone" to phoneEditText.text.toString()
            )
            saveUserCredentials(updatedUser)
        }

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

    //här tar vi emot callbackfunktionen och säger till den att den inte ska retunera något med Unit. Vi hämtar datan/credentials från databasen
    private fun loadUserCredentials(callback: (user: Map<String, Any>) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        callback(document.data ?: emptyMap())
                    } else {
                        Log.d("Aleksander", "finns ej detta dokument")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Aleksander", "get misslykades med:", exception)
                }
        }
    }

    //Vi "settar" datan vi har fyllt i när vi trycker knappen och uppdaterar datan för varje user
    private fun saveUserCredentials(user: Map<String, Any>) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            db.collection("users").document(userId)
                .set(user)
                .addOnSuccessListener {
                    Toast.makeText(this, "Credentials updated", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    Log.d("Aleksander", "error:", e)
                }
        }
    }
}