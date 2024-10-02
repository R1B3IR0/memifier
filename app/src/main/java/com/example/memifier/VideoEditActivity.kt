package com.example.memifier

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity

class VideoEditActivity : AppCompatActivity() {

    private lateinit var videoView: VideoView
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.video_edit)

        videoView = findViewById(R.id.video_view)
        playButton = findViewById(R.id.play_button)
        pauseButton = findViewById(R.id.pause_button)

        val videoResId = intent.getIntExtra("videoResId", -1)

        if (videoResId != -1) {
            val videoUri = Uri.parse("android.resource://" + packageName + "/" + videoResId)
            videoView.setVideoURI(videoUri)

            playButton.setOnClickListener {
                videoView.start()
            }

            pauseButton.setOnClickListener {
                videoView.pause()
            }
        }
    }
}
