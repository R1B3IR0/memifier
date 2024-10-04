package com.example.memifier

import androidx.camera.core.AspectRatio
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role.Companion.RadioButton
import androidx.compose.ui.unit.dp

data class CustomAspectRatio(val label: String, val width: Int, val height: Int)

val aspectRatios = listOf(
    CustomAspectRatio("1:1 (Instagram)", 1, 1),
    CustomAspectRatio("16:9 (Youtube Thumb)", 16, 9),
    CustomAspectRatio("9:16 (Storie/Reels)", 9, 16)
)


@Composable
fun AspectRatioSelector(
    selectedAspectRatio: CustomAspectRatio,
    onAspectRatioChange: (CustomAspectRatio) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text("Select Aspect Ratio", style = MaterialTheme.typography.titleLarge)

        aspectRatios.forEach { aspectRatio ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onAspectRatioChange(aspectRatio) }
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = aspectRatio == selectedAspectRatio,
                    onClick = { onAspectRatioChange(aspectRatio) }
                )
                Text(
                    text = aspectRatio.label,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }
    }
}
