package com.example.intergrationv4

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.Request

val apiKeyGame = "946ec40e73ceccfef84795a86f54c0a65e67ad7a"

class game_API : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_game_api)

        val menuClick = findViewById<ImageButton>(R.id.menuButton)
        val searchGameButton = findViewById<Button>(R.id.searchGameButton)
        val gameImageView = findViewById<ImageView>(R.id.gameImageView)
        val gameNameTextView = findViewById<TextView>(R.id.gameNameTextView)
        val searchField = findViewById<EditText>(R.id.searchFieldGameEditTextText)

        //Knapp för ta emot vad användaren skriver in för namn, sen kalla på fetchGame som tar emot datan
        searchGameButton.setOnClickListener {
            val query = searchField.text.toString()
            if (query.isEmpty()) {
                Toast.makeText(this, "Please enter a game name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            fetchGame(query) { game ->
                runOnUiThread {
                    gameNameTextView.text = game.name
                    val imageUrl = game.image?.super_url ?: ""
                    Glide.with(this).load(imageUrl).into(gameImageView)
                }
            }
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

    data class GameResponse(
        @SerializedName("results") val results: List<Game>
    )

    data class Game(
        @SerializedName("name") val name: String,
        @SerializedName("image") val image: GameImage?
    )

    data class GameImage(
        @SerializedName("super_url") val super_url: String?
    )

    //Hämtar datan för spelet användaren söker på i JSON format och konverterar det
    private fun fetchGame(query: String, onSuccess: (Game) -> Unit) {
        Thread {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://www.giantbomb.com/api/search/?api_key=$apiKeyGame&format=json&query=\"$query\"&resources=game")
                .get()
                .build()

            try {
                val response = client.newCall(request).execute()
                val json = response.body?.string() ?: return@Thread
                val gameResponse = Gson().fromJson(json, GameResponse::class.java)
                val games = gameResponse.results

                runOnUiThread {
                    if (games.isNotEmpty()) {
                        val firstGame = games.first()
                        onSuccess(firstGame)
                    } else {
                        Toast.makeText(this, "No games found", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(this, "Error fetching game", Toast.LENGTH_SHORT).show()
                }
            }
        }.start()
    }
}
