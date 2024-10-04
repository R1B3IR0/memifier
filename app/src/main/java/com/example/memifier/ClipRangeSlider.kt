import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ClipRangeSlider(
    startTime: Float,
    endTime: Float,
    onStartTimeChange: (Float) -> Unit,
    onEndTimeChange: (Float) -> Unit,
    duration: Float
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 8.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(Color(0xFF121212)), // Dark background
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Displaying start time as an integer
            Text(
                text = "${startTime.toInt()}s",
                modifier = Modifier.padding(8.dp),
                color = Color.White, // White text color
                fontSize = 20.sp // Increased font size
            )

            Slider(
                value = startTime,
                onValueChange = onStartTimeChange,
                valueRange = 0f..duration,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFBB86FC), // Customize thumb color
                    activeTrackColor = Color(0xFF03DAC6), // Customize active track color
                    inactiveTrackColor = Color(0xFF4A4A4A) // Customize inactive track color
                )
            )

            // Displaying end time as an integer
            Text(
                text = "${endTime.toInt()}s",
                modifier = Modifier.padding(8.dp),
                color = Color.White, // White text color
                fontSize = 20.sp // Increased font size
            )

            Slider(
                value = endTime,
                onValueChange = onEndTimeChange,
                valueRange = 0f..duration,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 8.dp),
                colors = SliderDefaults.colors(
                    thumbColor = Color(0xFFBB86FC), // Customize thumb color
                    activeTrackColor = Color(0xFF03DAC6), // Customize active track color
                    inactiveTrackColor = Color(0xFF4A4A4A) // Customize inactive track color
                )
            )
        }
    }
}
