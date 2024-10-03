package com.example.memifier // Change to your package name

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.util.Log
import android.view.TextureView
import com.arthenica.ffmpegkit.FFmpegKit
import com.arthenica.ffmpegkit.FFmpegSession
import com.arthenica.ffmpegkit.ReturnCode
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.IOException

fun captureClip(context: Context, videoPath: String, startTime: Float, endTime: Float) {
    val outputPath = "${context.getExternalFilesDir(null)}/captured_clip.mp4"

    // Calculate the duration of the clip
    val duration = endTime - startTime

    // FFmpeg command to capture a clip from `startTime` to `endTime`
    val ffmpegCommand = arrayOf(
        "-y",
        "-i", videoPath,
        "-ss", startTime.toString(), // Start time for the clip
        "-t", duration.toString(), // Duration of the clip
        outputPath
    )

    // Execute FFmpeg command asynchronously
    FFmpegKit.executeAsync(ffmpegCommand.joinToString(" ")) { session ->
        val returnCode = session.returnCode
        if (ReturnCode.isSuccess(returnCode)) {
            Log.d("FFmpeg", "Clip captured successfully: $outputPath")
            // Save the captured clip to gallery
            convertClipToGif(context, outputPath) // Pass the text to convertClipToGif
        } else {
            Log.e("FFmpeg", "Error capturing clip: $returnCode")
        }
    }
}



fun convertClipToGif(context: Context, clipPath: String) {
    val gifPath = "${context.getExternalFilesDir(null)}/captured_clip.gif"

    // FFmpeg command to convert the captured clip to GIF with text overlay using a native font
    val ffmpegCommand = arrayOf(
        "-y",
        "-i", clipPath,
        "-vf", "fps=10,scale=320:-1:flags=lanczos",
        gifPath
    )

    FFmpegKit.executeAsync(ffmpegCommand.joinToString(" ")) { session ->
        val returnCode = session.returnCode
        if (ReturnCode.isSuccess(returnCode)) {
            Log.d("FFmpeg", "GIF created successfully: $gifPath")
            // Save the GIF to the gallery
            saveGifToGallery(context, gifPath)
        } else {
            Log.e("FFmpeg", "Error converting to GIF: $returnCode")
        }
    }
}



fun captureCurrentFrame(videoUri: String, context: Context, onFrameCaptured: (Bitmap?) -> Unit) {
    val retriever = MediaMetadataRetriever()

    try {
        // Set the data source using the URI string
        retriever.setDataSource(context, Uri.parse(videoUri))  // Parse the string to Uri

        // Specify the time to capture (in microseconds)
        val timeUs = 5000000 // 5 seconds in microseconds
        val bitmap = retriever.getFrameAtTime(timeUs.toLong(), MediaMetadataRetriever.OPTION_CLOSEST_SYNC)

        onFrameCaptured(bitmap)
    } catch (e: Exception) {
        Log.e("VideoPlayer", "Error capturing frame: ${e.message}")
        onFrameCaptured(null)
    } finally {
        retriever.release()
    }
}


fun saveGifToGallery(context: Context, gifFilePath: String) {
    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "my_gif_${System.currentTimeMillis()}.gif")
        put(MediaStore.Images.Media.MIME_TYPE, "image/gif")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyApp")
    }

    val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

    uri?.let { gifUri ->
        try {
            context.contentResolver.openOutputStream(gifUri).use { outputStream ->
                outputStream?.let {
                    FileInputStream(gifFilePath).use { inputStream ->
                        inputStream.copyTo(it) // Copy GIF data to output stream
                    }
                    Log.d("Save GIF", "GIF saved to gallery!")
                }
            }
        } catch (e: IOException) {
            Log.e("Save GIF", "Failed to save GIF: ${e.message}")
        }
    }
}

fun saveClipToGallery(context: Context, clipPath: String) {
    val contentValues = ContentValues().apply {
        put(MediaStore.Video.Media.DISPLAY_NAME, "captured_clip_${System.currentTimeMillis()}.mp4")
        put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        put(MediaStore.Video.Media.RELATIVE_PATH, Environment.DIRECTORY_MOVIES + "/MyApp") // Specify your directory
    }

    val uri = context.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, contentValues)

    uri?.let { videoUri ->
        try {
            context.contentResolver.openOutputStream(videoUri).use { outputStream ->
                outputStream?.let { outStream ->
                    FileInputStream(clipPath).use { inputStream ->
                        inputStream.copyTo(outStream) // Copy video data to output stream
                    }
                    Log.d("Save Video", "Video saved to gallery!")
                }
            }
        } catch (e: IOException) {
            Log.e("Save Video", "Failed to save video: ${e.message}")
        }
    }
}

fun copyResourceToFile(context: Context, resourceId: Int): File {
    val inputStream = context.resources.openRawResource(resourceId)
    val outputFile = File(context.cacheDir, "temp_video.mp4") // You can change the file name if needed

    FileOutputStream(outputFile).use { outputStream ->
        inputStream.copyTo(outputStream)
    }

    return outputFile
}




