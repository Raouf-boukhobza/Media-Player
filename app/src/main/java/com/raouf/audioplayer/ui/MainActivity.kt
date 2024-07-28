package com.raouf.audioplayer.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.PageSize.Fill.calculateMainAxisPageSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.raouf.audioplayer.ui.theme.AudioPlayerTheme
import com.raouf.audioplayer.ui.theme.purpule
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AudioPlayerTheme {
              ActivityCompat.requestPermissions(this,
                  arrayOf(Manifest.permission.READ_MEDIA_AUDIO),
                  0
              )




            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HalfCircleSlider() {
    var angle by remember { mutableStateOf(0f) }
    val radius = 150f
    Canvas(
        modifier = Modifier
            .size(200.dp)
            .padding(16.dp)
            .pointerInput(Unit) {
                detectDragGestures { change, _ ->
                    val x = change.position.x - radius
                    val y = change.position.y - radius
                    val theta = atan2(y, x)
                    val newAngle = (theta * (180 / Math.PI) + 180).toFloat()
                    if (newAngle in 0f..190f){
                        angle = newAngle
                    }
                }
            }
    ) {
        // Draw the half circle arc
        drawArc(
            color = Color.LightGray,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            size = Size(radius * 2, radius * 2),
            topLeft = Offset(0f, 0f),
            style = Stroke(width = 14f)
        )
        drawArc(
            color = purpule,
            startAngle = 180f,
            sweepAngle = angle,
            useCenter = false,
            size = Size(radius * 2, radius * 2),
            topLeft = Offset(0f, 0f),
            style = Stroke(width = 14f),
        )
        // Calculate the thumb position
        val thumbX = radius + radius * cos((angle - 180) * (PI / 180)).toFloat()
        val thumbY = radius + radius * sin((angle - 180) * (PI / 180)).toFloat()

        // Draw the thumb
        drawCircle(
            color = purpule,
            radius = 16f,
            center = Offset(thumbX, thumbY)
        )
    }
}