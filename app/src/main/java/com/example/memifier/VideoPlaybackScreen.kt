package com.example.memifier

import android.net.Uri
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import androidx.compose.foundation.background
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.compose.runtime.remember
import androidx.compose.runtime.DisposableEffect
import androidx.media3.common.Player

@Composable
fun VideoPlaybackScreen(navController: NavController, uri: Uri) {
    val context = LocalContext.current

    // Estado para controlar se o vídeo está tocando ou pausado
    var isPlaying by remember { mutableStateOf(true) }
    var hasEnded by remember { mutableStateOf(false) } // Estado para saber se o vídeo terminou

    // ExoPlayer para reproduzir o vídeo
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            val mediaItem = MediaItem.fromUri(uri)
            setMediaItem(mediaItem)
            prepare()
            playWhenReady = true // Começa reproduzindo

            // Listener para verificar quando o vídeo termina
            addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    if (state == Player.STATE_ENDED) {
                        hasEnded = true // Vídeo terminou
                        isPlaying = false
                    }
                }
            })
        }
    }

    // Função para alternar entre play e pause ou reiniciar se terminou
    fun togglePlayPause() {
        if (hasEnded) {
            exoPlayer.seekTo(0) // Reinicia o vídeo
            exoPlayer.playWhenReady = true
            isPlaying = true
            hasEnded = false
        } else {
            if (isPlaying) {
                exoPlayer.pause()
            } else {
                exoPlayer.play()
            }
            isPlaying = !isPlaying
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            // Detecta o clique na tela para alternar entre play/pause ou reiniciar se terminou
            .pointerInput(Unit) {
                detectTapGestures {
                    togglePlayPause()
                }
            }
    ) {
        // VideoPlayer ocupa a tela inteira
        AndroidView(
            factory = {
                PlayerView(it).apply {
                    player = exoPlayer
                    useController = false // Desativa os controles padrões do PlayerView
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        // Coluna para os botões "Voltar" e "Clipar" na parte inferior
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            verticalArrangement = Arrangement.Bottom,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Botão "Voltar"
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.Transparent)
                ) {
                    Text(text = "Voltar", color = Color.White)
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Botão "Clipar"
                Button(
                    onClick = {
                        // Função para clipe de vídeo
                    },
                    modifier = Modifier
                        .weight(1f)
                        .background(Color.Transparent)
                ) {
                    Text(text = "Clipar", color = Color.White)
                }
            }
        }
    }

    // Releasing ExoPlayer when the screen is disposed
    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }
}
