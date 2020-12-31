package com.example.musicplayer

import android.annotation.SuppressLint
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var mediaPlayer: MediaPlayer
    private lateinit var runnable:Runnable
    private var handler: Handler = Handler()
    private var pause:Boolean = false

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val playBtn: ImageView = findViewById(R.id.playBtn)
        val pauseBtn: ImageView = findViewById(R.id.playBtn)
        val stopBtn: ImageView = findViewById(R.id.playBtn)
        val seekBAR: SeekBar = findViewById(R.id.seek_bar)
        val tvPASS: TextView = findViewById(R.id.tv_pass)
        val tvDUE: TextView = findViewById(R.id.tv_due)

        // Start the media player
        playBtn.setOnClickListener{
            if(pause){
                mediaPlayer.seekTo(mediaPlayer.currentPosition)
                mediaPlayer.start()
                pause = false
                Toast.makeText(this, "media playing", Toast.LENGTH_SHORT).show()
            }else{
                mediaPlayer.start()
                Toast.makeText(this, "media playing", Toast.LENGTH_SHORT).show()
            }
            initializeSeekBar()
            playBtn.isEnabled = false
            pauseBtn.isEnabled = true
            stopBtn.isEnabled = true

            mediaPlayer.setOnCompletionListener {
                playBtn.isEnabled = true
                pauseBtn.isEnabled = false
                stopBtn.isEnabled = false
                Toast.makeText(this, "end", Toast.LENGTH_SHORT).show()
            }
        }

        // Pause the media player
        pauseBtn.setOnClickListener {
            if(mediaPlayer.isPlaying){
                mediaPlayer.pause()
                pause = true
                playBtn.isEnabled = true
                pauseBtn.isEnabled = false
                stopBtn.isEnabled = true
                Toast.makeText(this, "media pause", Toast.LENGTH_SHORT).show()
            }
        }

        // Stop the media player
        stopBtn.setOnClickListener{
            if(mediaPlayer.isPlaying || pause){
                pause = false
                seekBAR.progress = 0
                mediaPlayer.stop()
                mediaPlayer.reset()
                mediaPlayer.release()
                handler.removeCallbacks(runnable)

                playBtn.isEnabled = true
                pauseBtn.isEnabled = false
                stopBtn.isEnabled = false
                tvPASS.text = ""
                tvDUE.text = ""
                Toast.makeText(this, "media stop", Toast.LENGTH_SHORT).show()
            }
        }
        // Seek bar change listener
        seekBAR.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                if (b) {
                    mediaPlayer.seekTo(i * 1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
            }
        })
    }

    // Method to initialize seek bar and audio stats
    @SuppressLint("SetTextI18n")
    private fun initializeSeekBar() {
        val seekBAR: SeekBar = findViewById(R.id.seek_bar)
        val tvPASS: TextView = findViewById(R.id.tv_pass)
        val tvDUE: TextView = findViewById(R.id.tv_due)

        seekBAR.max = mediaPlayer.seconds

        runnable = Runnable {
            seekBAR.progress = mediaPlayer.currentSeconds

            tvPASS.text = "${mediaPlayer.currentSeconds} sec"
            val diff = mediaPlayer.seconds - mediaPlayer.currentSeconds
            tvDUE.text = "$diff sec"

            handler.postDelayed(runnable, 1000)
        }
        handler.postDelayed(runnable, 1000)
    }
}

// Creating an extension property to get the media player time duration in seconds
val MediaPlayer.seconds:Int
    get() {
        return this.duration / 1000
    }
// Creating an extension property to get media player current position in seconds
val MediaPlayer.currentSeconds:Int
    get() {
        return this.currentPosition/1000
    }