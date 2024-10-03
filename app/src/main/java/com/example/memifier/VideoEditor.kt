package com.example.memifier

import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VideoEditor(videoUri: Uri, onExport: (Uri) -> Unit) {
    var memeText by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Exibe o vídeo selecionado
        VideoPlayer(uri = videoUri)

        Spacer(modifier = Modifier.height(16.dp))

        // Input de texto para customizar o vídeo
        OutlinedTextField(
            value = memeText,
            onValueChange = { memeText = it },
            label = { Text("Texto do Meme") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botão para exportar vídeo
        Button(onClick = { /* Lógica para cortar e exportar */ }) {
            Text("Exportar Meme")
        }
    }
}