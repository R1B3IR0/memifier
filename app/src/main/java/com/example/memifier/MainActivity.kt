package com.example.memifier

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.media.MediaMetadataRetriever
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.ui.PlayerView
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.io.IOException


data class PositionedText(val text: String, var x: Float, var y: Float)

class MainActivity : ComponentActivity() {
    private lateinit var player: ExoPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize ExoPlayer
        player = ExoPlayer.Builder(this).build()

        setContent {
            VideoPlayerApp(player = player)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        player.release() // Release the player when the activity is destroyed
    }
}

@Composable
fun VideoPlayerApp(player: ExoPlayer) {
    var selectedVideoUri by remember { mutableStateOf<String?>(null) }

    if (selectedVideoUri != null) {
        VideoPlayerScreen(player = player, videoUri = selectedVideoUri!!)
    } else {
        // Getting context from LocalContext
        val context = LocalContext.current
        VideoListScreen { videoName ->
            val videoResourceId = when (videoName) {
                "video1.mp4" -> R.raw.video1
                "video2.mp4" -> R.raw.video3 // Make sure this resource exists
                else -> R.raw.video1
            }

            // Copy resource to a file
            val videoFile = copyResourceToFile(context, videoResourceId)
            val videoUri = videoFile.toURI().toString() // Get the file URI

            selectedVideoUri = videoUri

            // Prepare and play the selected video
            val mediaItem = MediaItem.fromUri(videoUri)
            player.setMediaItem(mediaItem)
            player.prepare()
            player.play()
        }
    }
}



@Composable
fun VideoListScreen(onVideoSelected: (String) -> Unit) {
    // List of available video names
    val videoNames = listOf("video1.mp4", "video2.mp4") // Add more video names as needed

    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(videoNames) { videoName ->
            VideoListItem(videoName = videoName) {
                // Pass the current context to the onVideoSelected function
                onVideoSelected(videoName)
            }
        }
    }
}

@Composable
fun VideoListItem(videoName: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = videoName, style = MaterialTheme.typography.bodyMedium)
            Button(onClick = onClick) {
                Text(text = "Play")
            }
        }
    }
}

val impactFontFamily = FontFamily(Font(R.font.impact)) // Assuming impact.ttf is placed in res/font directory

@Composable
fun VideoPlayerScreen(player: ExoPlayer, videoUri: String) {
    var capturedFrame by remember { mutableStateOf<Bitmap?>(null) }
    var isCapturing by remember { mutableStateOf(false) }
    var isClipMode by remember { mutableStateOf(false) }
    var clipStartTime by remember { mutableStateOf(0f) }
    var clipEndTime by remember { mutableStateOf(10f) } // Default 10 seconds
    val context = LocalContext.current

    capturedFrame?.let { bitmap ->
        // Display captured frame
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Captured Frame",
                modifier = Modifier.fillMaxSize()
            )

            // Save Button for Frame
            Button(
                onClick = {
                    saveBitmapWithTextToGallery(context, bitmap, "Your Text") // Optional: Keep some default text if needed
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text("Save Meme")
            }
        }
    } ?: run {
        // Video Player and Controls
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (playerViewRef, controlsLayout) = createRefs()

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
                        top.linkTo(parent.top)
                        bottom.linkTo(controlsLayout.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            )

            // Controls: Capture Frame and Capture Clip
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
                // Capture Frame Button
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

                // Capture Clip Button
                Button(
                    onClick = {
                        isClipMode = true // Enable clip mode to show the slider
                    },
                    modifier = Modifier.padding(bottom = 8.dp)
                ) {
                    Text("Capture Clip")
                }

                // Range Slider for clip selection (visible only in clip mode)
                if (isClipMode) {
                    ClipRangeSlider(
                        clipStartTime,
                        clipEndTime,
                        onStartTimeChange = { clipStartTime = it.coerceIn(0f, clipEndTime) },
                        onEndTimeChange = { clipEndTime = it.coerceIn(clipStartTime, (player.duration.toFloat() / 1000f)) },
                        duration = player.duration.toFloat() / 1000f // Convert to seconds
                    )

                    // Button to confirm and capture the clip
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



@Composable
fun ClipRangeSlider(
    startTime: Float,
    endTime: Float,
    onStartTimeChange: (Float) -> Unit,
    onEndTimeChange: (Float) -> Unit,
    duration: Float
) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Text(text = String.format("Start: %.2fs", startTime), modifier = Modifier.padding(8.dp))
        Slider(
            value = startTime,
            onValueChange = onStartTimeChange,
            valueRange = 0f..duration,
            modifier = Modifier.weight(1f)
        )
        Text(text = String.format("End: %.2fs", endTime), modifier = Modifier.padding(8.dp))
        Slider(
            value = endTime,
            onValueChange = onEndTimeChange,
            valueRange = 0f..duration,
            modifier = Modifier.weight(1f)
        )
    }
}



// Function to save bitmap with text to the gallery
fun saveBitmapWithTextToGallery(context: Context, bitmap: Bitmap, text: String) {
    // Create a new bitmap to draw the text on
    val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(mutableBitmap)
    val paint = Paint().apply {
        color = android.graphics.Color.BLACK // Change to white for better visibility
        textSize = 120f // Set your desired text size
        typeface = android.graphics.Typeface.create("sans-serif-condensed", android.graphics.Typeface.BOLD)
    }

    // Draw the text on the canvas
    canvas.drawText(text, 100f, 100f, paint) // Adjust position as needed

    // Prepare to save the bitmap
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "meme_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyApp") // For Android 10+
    }

    // Insert the image into the MediaStore
    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    uri?.let { imageUri ->
        try {
            context.contentResolver.openOutputStream(imageUri).use { outputStream ->
                if (outputStream != null) {
                    mutableBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                    outputStream.flush()
                    Log.d("Save Bitmap", "Image saved to gallery!")
                }
            }
        } catch (e: IOException) {
            Log.e("Save Bitmap", "Failed to save image: ${e.message}")
        }
    }
}

