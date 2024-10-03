package com.example.memifier

import android.content.Context
import android.content.Intent
import android.net.Uri

fun shareContent(context: Context, uri: Uri, type: String) {
    // Cria um Intent de compartilhamento com o tipo especificado (GIF ou MP4)
    val intent = Intent(Intent.ACTION_SEND).apply {
        this.type = type // "image/gif" ou "video/mp4"
        putExtra(Intent.EXTRA_STREAM, uri)
    }

    // Inicia o Intent de compartilhamento
    context.startActivity(Intent.createChooser(intent, "Compartilhar Meme"))
}