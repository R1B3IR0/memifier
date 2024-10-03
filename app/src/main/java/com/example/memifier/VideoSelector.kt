package com.example.memifier

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun VideoSelectionScreen(navController: NavController) {
    val availableVideos = listOf(
        Pair(R.raw.video1, "Vídeo 1"),
        Pair(R.raw.video2, "Vídeo 2"),
        Pair(R.raw.video3, "Vídeo 3"),
        Pair(R.raw.video4, "Vídeo 4"),
        Pair(R.raw.video5, "Vídeo 5"),
        Pair(R.raw.video6, "Vídeo 6"),
        Pair(R.raw.video7, "Vídeo 7"),
        Pair(R.raw.video8, "Vídeo 8"),
        Pair(R.raw.video9, "Vídeo 9"),
        Pair(R.raw.video10, "Vídeo 10"),
        Pair(R.raw.video11, "Vídeo 11"),
        Pair(R.raw.video12, "Vídeo 12"),
        Pair(R.raw.video13, "Vídeo 13"),
    )

    // Armazenar a seleção do vídeo
    var selectedVideo by remember { mutableStateOf<Uri?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(text = "Selecione um Vídeo", style = MaterialTheme.typography.headlineMedium)

        for ((videoRes, videoName) in availableVideos) {
            val uri =
                Uri.parse("android.resource://${MainActivity::class.java.`package`?.name}/$videoRes")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        selectedVideo = uri // Atualiza a seleção para o vídeo clicado
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = selectedVideo == uri, // Verifica se o vídeo atual é o selecionado
                    onCheckedChange = {
                        selectedVideo = if (selectedVideo == uri) {
                            null // Desmarcar se já estiver selecionado
                        } else {
                            uri // Selecionar o novo vídeo
                        }
                    }
                )
                Text(text = videoName, fontSize = 18.sp) // Exibir o nome do vídeo
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            selectedVideo?.let { uri ->
                // Codificando o URI antes de passar para a navegação
                navController.navigate("video_playback/${Uri.encode(uri.toString())}")
            }
        }) {
            Text("Continuar")
        }
    }
}
