package com.example.intergrationv4

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.android.gms.common.api.Response
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import okhttp3.Request

val apiKeyTMDB = "79dff56fdf6ac30dc3bafc85693f9fcb"

class TMDB_API : AppCompatActivity() {
    private val CHANNEL_ID = "movie_channel"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tmdb_api)

        createNotificationChannel()

        val menuClick = findViewById<ImageButton>(R.id.menuButton)
        val generateMovieButton = findViewById<Button>(R.id.generateMoviesButton)
        val moviesImageView = findViewById<ImageView>(R.id.moviesImageView)
        val movieNameTextView = findViewById<TextView>(R.id.movieNameTextView)


        //knapp som kallar på fetchTopMovie för att hämta datan och displayar namn och bild på filmen.
        generateMovieButton.setOnClickListener() {
            fetchTopMovie { movie ->
                runOnUiThread() {
                    movieNameTextView.text = movie.title
                    val imageUrl = "https://image.tmdb.org/t/p/w500${movie.poster_path}"
                    Glide.with(this).load(imageUrl).into(moviesImageView)
                    sendNotification(movie.title)
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


    data class MovieResponse(
        @SerializedName("results") val results: List<Movie>
    )

    data class Movie(
        @SerializedName("title") val title: String,
        @SerializedName("poster_path") val poster_path: String
    )


    //notifikation när man hämtar en film
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Movie Fetch Notifications"
            val descriptionText = "Notifies when a new movie is fetched"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager: NotificationManager =
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun sendNotification(movieTitle: String) {
        val intent = Intent(this, TMDB_API::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle("New Movie Fetched")
            .setContentText("Movie: $movieTitle")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(1001, builder.build())
        }
    }


    //Liknande Game API så hämtar vi datan från databasen och konverterar JSON
    private fun fetchTopMovie(onSuccess: (Movie) -> Unit) {
        Thread {
            val client = OkHttpClient()
            val request = Request.Builder()
                .url("https://api.themoviedb.org/3/movie/top_rated?language=en-US&page=1&api_key=$apiKeyTMDB")
                .get()
                .build()

            try {
                val response: okhttp3.Response = client.newCall(request).execute()
                val json = response.body?.string() ?: return@Thread
                val movieResponse = Gson().fromJson(json, MovieResponse::class.java)
                val movies = movieResponse.results

                if (movies.isNotEmpty()) {
                    val randomMovie = movies.random()
                    runOnUiThread {
                        onSuccess(randomMovie)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }
}

//referenser:
//https://github.com/HasanElfalt/TMDB/blob/develop/app/src/main/java/com/elfalt/tmdb/ui/MovieDetailsViewModel.kt
//https://reintech.io/blog/implementing-movie-discovery-app-android-tmdb-api
//https://developer.themoviedb.org/reference/movie-top-rated-list
//https://developer.android.com/develop/ui/views/notifications/build-notification
//Chatgpt