package com.example.memifier

import ClipRangeSlider
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Paint
import android.media.MediaMetadataRetriever
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.memifier.ui.theme.MemifierTheme
import com.github.skydoves.colorpicker.compose.HsvColorPicker
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.ui.PlayerView
import com.github.skydoves.colorpicker.compose.*

@Composable
fun VideoPlayerScreen(player: ExoPlayer, videoUri: String, onBack: () -> Unit) {
    var capturedFrame by remember { mutableStateOf<Bitmap?>(null) }
    var isCapturing by remember { mutableStateOf(false) }
    var isClipMode by remember { mutableStateOf(false) }
    var clipStartTime by remember { mutableStateOf(0f) }
    var clipEndTime by remember { mutableStateOf(10f) }
    var memeText by remember { mutableStateOf("") }
    var textPosition by remember { mutableStateOf(Offset(100f, 100f)) }
    val context = LocalContext.current
    var selectedAspectRatio by remember { mutableStateOf(aspectRatios[0]) }
    val defaultTextColor = MaterialTheme.colorScheme.onPrimary
    var textColor by remember { mutableStateOf(defaultTextColor) }
    var textSize by remember { mutableStateOf(24.sp) }

    MemifierTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            capturedFrame?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Captured Frame",
                    modifier = Modifier.fillMaxSize()
                )

                if (memeText.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .offset { IntOffset(textPosition.x.toInt(), textPosition.y.toInt()) }
                            .pointerInput(Unit) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    textPosition += dragAmount
                                }
                            }
                            .padding(8.dp)
                    ) {
                        Text(
                            text = memeText,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(8.dp),
                            color = textColor,
                            fontSize = textSize,
                            style = MaterialTheme.typography.titleLarge
                        )
                    }
                }

                AspectRatioSelector(selectedAspectRatio) { aspectRatio ->
                    selectedAspectRatio = aspectRatio
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    OutlinedTextField(
                        value = memeText,
                        onValueChange = { memeText = it },
                        label = { Text("Enter meme text") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Text Size: ${textSize.value.toInt()}sp")
                    Slider(
                        value = textSize.value,
                        onValueChange = { textSize = it.sp },
                        valueRange = 12f..48f,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text("Text Color")
                    ColorPicker(
                        selectedColor = textColor,
                        onColorSelected = { textColor = it }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val resizedBitmap = resizeBitmap(bitmap, selectedAspectRatio)
                            saveBitmapWithTextToGallery(context, resizedBitmap, memeText)
                        }
                    ) {
                        Text("Save Meme")
                    }
                }
            } ?: run {
                AndroidView(
                    factory = { context ->
                        PlayerView(context).apply {
                            this.player = player
                            setPadding(8, 0, 8, 0)
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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

                    Button(
                        onClick = {
                            isClipMode = true
                        },
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text("Capture Clip")
                    }

                    Button(
                        onClick = { onBack() },
                        modifier = Modifier.padding(bottom = 8.dp)
                    ) {
                        Text("Back")
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (isClipMode) {
                        ClipRangeSlider(
                            clipStartTime,
                            clipEndTime,
                            onStartTimeChange = { clipStartTime = it.coerceIn(0f, clipEndTime) },
                            onEndTimeChange = { clipEndTime = it.coerceIn(clipStartTime, (player.duration.toFloat() / 1000f)) },
                            duration = player.duration.toFloat() / 1000f
                        )

                        OutlinedTextField(
                            value = memeText,
                            onValueChange = { memeText = it },
                            label = { Text("Enter meme text") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        AspectRatioSelector(selectedAspectRatio) { aspectRatio ->
                            selectedAspectRatio = aspectRatio
                        }

                        Button(
                            onClick = {
                                captureClip(context, videoUri, clipStartTime, clipEndTime, memeText, textPosition, selectedAspectRatio)
                                isClipMode = false
                            }
                        ) {
                            Text("Confirm Clip Capture")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColorPicker(selectedColor: Color, onColorSelected: (Color) -> Unit) {
    var color by remember { mutableStateOf(selectedColor) }
    val controller = remember { ColorPickerController() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        HsvColorPicker(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            controller = controller,
            onColorChanged = { colorEnvelope: ColorEnvelope ->
                color = colorEnvelope.color
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { onColorSelected(color) }) {
            Text("Select Color")
        }
    }
}