package com.example.memifier

import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem

@Composable
fun VideoPlayerApp(player: ExoPlayer) {
    var selectedVideoUri by remember { mutableStateOf<String?>(null) }

    if (selectedVideoUri != null) {
        VideoPlayerScreen(player = player,
            videoUri = selectedVideoUri!!,
            onBack = { selectedVideoUri = null }
        )
    } else {
        val context = LocalContext.current
        VideoListScreen { videoName ->
            val videoResourceId = when (videoName) {
                "video1.mp4" -> R.raw.video1
                "video2.mp4" -> R.raw.video3
                else -> R.raw.video1
            }

            val videoFile = copyResourceToFile(context, videoResourceId)
            val videoUri = videoFile.toURI().toString()

            selectedVideoUri = videoUri

            val mediaItem = MediaItem.fromUri(videoUri)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        }
    }
}
