package com.example.memifier

import ClipRangeSlider
import android.content.Context
import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.PlayerView

@Composable
fun VideoPlayerScreen(player: ExoPlayer, videoUri: String, onBack: () -> Unit) {
    var capturedFrame by remember { mutableStateOf<Bitmap?>(null) }
    var isCapturing by remember { mutableStateOf(false) }
    var isClipMode by remember { mutableStateOf(false) }
    var clipStartTime by remember { mutableStateOf(0f) }
    var clipEndTime by remember { mutableStateOf(10f) }
    val context = LocalContext.current

    capturedFrame?.let { bitmap ->
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Captured Frame",
                modifier = Modifier.fillMaxSize()
            )

            Button(
                onClick = {
                    saveBitmapWithTextToGallery(context, bitmap, "Your Text")
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text("Save Meme")
            }
        }
    } ?: run {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (backButtonRef, playerViewRef, controlsLayout) = createRefs()

            // Back Button
            Button(
                onClick = { onBack() },
                modifier = Modifier
                    .constrainAs(backButtonRef) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    }
                    .padding(16.dp)
            ) {
                Text("Back")
            }

            // ExoPlayer View
            AndroidView(
                factory = { context ->
                    PlayerView(context).apply {
                        this.player = player
                        setPadding(8, 0, 8, 0)
                    }
                },
                modifier = Modifier
                    .constrainAs(playerViewRef) {
                        top.linkTo(backButtonRef.bottom, margin = 8.dp)
                        bottom.linkTo(controlsLayout.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            // Controls Layout
            Column(
                modifier = Modifier
                    .constrainAs(controlsLayout) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Button(
                    onClick = {
                        if (!isCapturing) {
                            isCapturing = true
                            captureCurrentFrame(videoUri, context) { bitmap ->
                                capturedFrame = bitmap
                                isCapturing = false
                            }
                        }
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text("Capture Frame")
                }

                Button(
                    onClick = {
                        isClipMode = true
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text("Capture Clip")
                }

                if (isClipMode) {
                    ClipRangeSlider(
                        clipStartTime,
                        clipEndTime,
                        onStartTimeChange = { clipStartTime = it.coerceIn(0f, clipEndTime) },
                        onEndTimeChange = { clipEndTime = it.coerceIn(clipStartTime, (player.duration.toFloat() / 1000f)) },
                        duration = player.duration.toFloat() / 1000f // Convert to seconds
                    )

                    Button(
                        onClick = {
                            captureClip(context, videoUri, clipStartTime, clipEndTime) // Now without text
                            isClipMode = false // Hide slider after capturing clip
                        }
                    ) {
                        Text("Confirm Clip Capture")
                    }
                }
            }
        }
    }
}
