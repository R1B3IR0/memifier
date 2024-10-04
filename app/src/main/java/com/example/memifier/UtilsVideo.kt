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
import androidx.compose.ui.geometry.Offset
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.FileInputStream
import java.io.IOException

fun captureClip(context: Context, videoPath: String, startTime: Float, endTime: Float, text: String, textPostion: Offset, aspectRatio: CustomAspectRatio) {
    val outputPath = "${context.getExternalFilesDir(null)}/captured_clip.mp4"

    val duration = endTime - startTime

    val ffmpegCommand = arrayOf(
        "-y",
        "-i", videoPath,
        "-ss", startTime.toString(),
        "-t", duration.toString(),
        outputPath
    )

    FFmpegKit.executeAsync(ffmpegCommand.joinToString(" ")) { session ->
        val returnCode = session.returnCode
        if (ReturnCode.isSuccess(returnCode)) {
            Log.d("FFmpeg", "Clip captured successfully: $outputPath")
            convertClipToGif(context, outputPath, text, textPostion, aspectRatio)
        } else {
            Log.e("FFmpeg", "Error capturing clip: $returnCode")
        }
    }
}



fun convertClipToGif(context: Context, clipPath: String, text: String, textPosition: Offset, aspectRatio: CustomAspectRatio) {
    val gifPath = "${context.getExternalFilesDir(null)}/captured_clip.gif"

    val textX = textPosition.x.toString()
    val textY = textPosition.y.toString()

    val newWidth = 320
    val newHeight = (newWidth * aspectRatio.height / aspectRatio.width).toInt()

    val ffmpegCommand = arrayOf(
        "-y",
        "-i", clipPath,
        "-vf", "fps=10,scale=$newWidth:$newHeight:-1:flags=lanczos,drawtext=text='$text':x=$textX:y=$textY:fontsize=24:fontcolor=white:fontfile=/system/fonts/Roboto-Regular.ttf",
        gifPath
    )

    FFmpegKit.executeAsync(ffmpegCommand.joinToString(" ")) { session ->
        val returnCode = session.returnCode
        if (ReturnCode.isSuccess(returnCode)) {
            Log.d("FFmpeg", "GIF created successfully: $gifPath")
            saveGifToGallery(context, gifPath)
        } else {
            Log.e("FFmpeg", "Error converting to GIF: $returnCode")
        }
    }
}



fun captureCurrentFrame(videoUri: String, context: Context, onFrameCaptured: (Bitmap?) -> Unit) {
    val retriever = MediaMetadataRetriever()

    try {
        retriever.setDataSource(context, Uri.parse(videoUri))

        val timeUs = 5000000
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
                        inputStream.copyTo(it)
                    }
                    Log.d("Save GIF", "GIF saved to gallery!")
                }
            }
        } catch (e: IOException) {
            Log.e("Save GIF", "Failed to save GIF: ${e.message}")
        }
    }
}


fun copyResourceToFile(context: Context, resourceId: Int): File {
    val inputStream = context.resources.openRawResource(resourceId)
    val outputFile = File(context.cacheDir, "temp_video.mp4")

    FileOutputStream(outputFile).use { outputStream ->
        inputStream.copyTo(outputStream)
    }

    return outputFile
}




