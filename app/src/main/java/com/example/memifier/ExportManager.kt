package com.example.memifier

import android.net.Uri
import androidx.compose.runtime.Composable

@Composable
fun ExportManager(videoUri: Uri, isGif: Boolean, duration: Int) {
    // Lógica para exportar o vídeo como GIF ou MP4
    if (isGif) {
        // Exportar como GIF
    } else {
        // Exportar como vídeo MP4
    }
}