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
    val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
    val canvas = Canvas(mutableBitmap)
    val outlinePaint = Paint().apply {
        color = android.graphics.Color.BLACK
        textSize = 120f
        typeface = android.graphics.Typeface.create("sans-serif-condensed", android.graphics.Typeface.BOLD)
        style = Paint.Style.STROKE
        strokeWidth = 10f
        strokeJoin = Paint.Join.ROUND
        strokeMiter = 10f
        isAntiAlias = true
    }

    val fillPaint = Paint().apply {
        color = android.graphics.Color.WHITE
        textSize = 120f
        typeface = android.graphics.Typeface.create("sans-serif-condensed", android.graphics.Typeface.BOLD)
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    canvas.drawText(text, 100f, 100f, outlinePaint)
    canvas.drawText(text, 100f, 100f, fillPaint)

    val contentValues = ContentValues().apply {
        put(MediaStore.Images.Media.DISPLAY_NAME, "meme_${System.currentTimeMillis()}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/MyApp")
    }

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

fun resizeBitmap(bitmap: Bitmap, aspectRatio: CustomAspectRatio): Bitmap {
    val newWidth = bitmap.width
    val newHeight = (newWidth * aspectRatio.height / aspectRatio.width).toInt()
    return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false)
}
