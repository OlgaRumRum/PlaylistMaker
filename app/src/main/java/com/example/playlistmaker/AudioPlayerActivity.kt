package com.example.playlistmaker

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.Locale

class AudioPlayerActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_player)

        val artworkUrl = findViewById<ImageView>(R.id.artWork_audio_player)
        val trackName = findViewById<TextView>(R.id.trackName_audio_player)
        val artist = findViewById<TextView>(R.id.artist)

        val duration = findViewById<TextView>(R.id.trackTime)
        val album = findViewById<TextView>(R.id.albumName)
        val year = findViewById<TextView>(R.id.yearName)
        val genre = findViewById<TextView>(R.id.genreName)
        val country = findViewById<TextView>(R.id.countryName)


        val backArrowButton = findViewById<ImageButton>(R.id.back_arrow_button)
        backArrowButton.setOnClickListener { finish() }

        val track: Track? = intent.getParcelableExtra(SearchActivity.TRACK_KEY)

        if (track != null) {
            trackName.text = track.trackName
            artist.text = track.artistName
            duration.text =
                SimpleDateFormat("mm:ss", Locale.getDefault()).format(track.trackTimeMillis)
            album.text = track.collectionName.ifEmpty { "" }
            year.text = track.releaseDate.substring(0, 4)
            genre.text = track.primaryGenreName
            country.text = track.country
        } else {
            showErrorDialog()
        }

        Glide.with(this)
            .load(track?.artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg"))
            .placeholder(R.drawable.placeholder)
            .centerCrop()
            .transform(RoundedCorners(applicationContext.resources.getDimensionPixelSize(R.dimen.circleRadius_artwork)))
            .into(artworkUrl)

    }

    private fun showErrorDialog() {
        val dialog = AlertDialog.Builder(this)
            .setMessage("Ошибка: трек не найден.")
            .setPositiveButton("OK") { dialog, which ->
                // Действия при нажатии на кнопку OK, например, закрытие активити
                finish()
            }
            .create()
        dialog.show()
    }

}

