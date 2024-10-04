package com.example.memifier

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.provider.MediaStore
import android.util.Log
import java.io.IOException

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
    } ?: run {
        Log.e("Save Bitmap", "Failed to get URI for image")
    }
}
