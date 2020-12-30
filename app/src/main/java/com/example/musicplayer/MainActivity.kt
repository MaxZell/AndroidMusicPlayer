package com.example.musicplayer

import android.annotation.SuppressLint
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
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

        val playBtn: Button = findViewById(R.id.playBtn)
        val pauseBtn: Button = findViewById(R.id.pauseBtn)
        val stopBtn: Button = findViewById(R.id.stopBtn)
        val seekBAR: SeekBar = findViewById(R.id.seek_bar)
        val tvPASS: TextView = findViewById(R.id.tv_pass)
        val tvDUE: TextView = findViewById(R.id.tv_due)

//        val mMediaPlayer = MediaPlayer().apply {
//            setDataSource(application, Uri.parse("test.mp3"))
//            setAudioAttributes(AudioAttributes.Builder()
//                .setUsage(AudioAttributes.USAGE_MEDIA)
//                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
//                .build()
//            )
//            prepare()
//        }
//        mMediaPlayer.start()

//        var path = super.getFilesDir().toString()
        var path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() // /storage/emulated/0/Android/data/com.example.musicplayer/files/Download
//        path = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absolutePath.toString().toString() // /storage/emulated/0/Android/data/com.example.musicplayer/files/Download
//        path += "/../.."
//        path = "/storage/emulated/0/Android/data/package/files/Download"
        Log.d("Files", "Path: $path")
        val directory = File(path)
        val files = directory.listFiles()
        Log.d("Files", "Size: " + files.size)
        for (i in files.indices) {
            Log.d("Files", "FileName:" + files[i].name)
//            if (files[i].isFile){
//                Log.d("Files", "FileName:" + files[i].name)
//            }else if(files[i].isDirectory){
//                Log.d("Files", "DirName:" + files[i].name)
//                val childDirectory = File(path + "/" + files[i].name)
//                Log.d("Files", "childDirectoryName: $childDirectory")
//                val childFiles = childDirectory.listFiles()
//                Log.d("Files", "childSize: " + childFiles.size)
//                for (f in childFiles.indices){
//                    Log.d("Files", "FileName:" + files[f].name)
//                }
//            }
        }

        // Start the media player
        playBtn.setOnClickListener{
            if(pause){
                mediaPlayer.seekTo(mediaPlayer.currentPosition)
                mediaPlayer.start()
                pause = false
                Toast.makeText(this, "media playing", Toast.LENGTH_SHORT).show()
            }else{
                val file = File(super.getExternalFilesDir(Environment.DIRECTORY_MUSIC), "test.mp3")
//                val path = super.getExternalFilesDir(Environment.DIRECTORY_MUSIC).toString() + "/Music/test.mp3"
//                var musicPath = super.getExternalFilesDir(Environment.DIRECTORY_MUSIC) + "/test.mp3"
                mediaPlayer = MediaPlayer.create(applicationContext, Uri.fromFile(file))
//                mediaPlayer = MediaPlayer.create(applicationContext, R.raw.test)
//                mediaPlayer.start()
                val mMediaPlayer = MediaPlayer().apply {
                    val bla = Uri.parse("test.mp3")
                    Log.d("Files", "bla: $bla")
                    setDataSource(applicationContext, Uri.parse("/storage/emulated/0/Android/data/package/files/Download/test.mp3"))
                    setAudioAttributes(AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                    )
                    prepare()
                }
                mMediaPlayer.start()
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