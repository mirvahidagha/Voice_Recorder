package com.kodachi.recorder

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Icon
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.io.File

class MainActivity : AppCompatActivity() {
    private var isRecording = false
    private var isPlaying = false
    private lateinit var rec: AudioRecorder
    private var player: MediaPlayer? = null
    lateinit var fabRecord: FloatingActionButton
    lateinit var fabPlay: FloatingActionButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))

        fabRecord = findViewById(R.id.fab_record)
        fabPlay = findViewById(R.id.fab_play)
        rec = AudioRecorder(this)
        fabRecord.setOnClickListener { view ->

            if (isRecording) {
                isRecording = false
                fabRecord.setImageIcon(Icon.createWithResource(this, R.drawable.baseline_mic_24))
                rec.stopRecord()
            } else {
                rec.startRecord()
                isRecording = true
                fabRecord.setImageIcon(
                    Icon.createWithResource(
                        this,
                        R.drawable.outline_stop_circle_24
                    )
                )
            }

        }
        fabPlay.setOnClickListener {
            if (isPlaying) {
                fabPlay.setImageIcon(
                    Icon.createWithResource(
                        this,
                        android.R.drawable.ic_media_play
                    )
                )
                player?.stop()
                player?.release()
                player = null
            } else if (File(AudioRecorder.WAVPath).exists()) {

                fabPlay.setImageIcon(
                    Icon.createWithResource(
                        this,
                        android.R.drawable.ic_media_pause
                    )
                )
                player = MediaPlayer.create(this, Uri.fromFile(File(AudioRecorder.WAVPath)))
                player?.setOnCompletionListener {
                    isPlaying = !isPlaying
                    fabPlay.setImageIcon(
                        Icon.createWithResource(
                            this,
                            android.R.drawable.ic_media_play
                        )
                    )

                }
                player?.start()
                isPlaying = !isPlaying
            } else Snackbar.make(this,it, "Heç bir səs yazısı yoxdur.", Snackbar.LENGTH_SHORT).show()

        }
        requestPermissions()

    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if ((ContextCompat.checkSelfPermission(
                    getApplicationContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ))
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.RECORD_AUDIO,
                    ), 0
                )
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (!Environment.isExternalStorageManager()) {
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    startActivity(intent)
                    return
                }
            }

        }
    }
}