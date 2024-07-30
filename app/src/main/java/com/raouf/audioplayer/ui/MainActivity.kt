package com.raouf.audioplayer.ui

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.raouf.audioplayer.R
import com.raouf.audioplayer.ui.theme.AudioPlayerTheme
import com.raouf.audioplayer.ui.theme.purpule
import dagger.hilt.android.AndroidEntryPoint


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
                Scaffold(modifier = Modifier.fillMaxSize()){paddingValues ->  
                       Column(modifier = Modifier
                           .fillMaxSize()
                           .padding(paddingValues),
                           verticalArrangement = Arrangement.Center,
                           horizontalAlignment = Alignment.CenterHorizontally){
                         MusicSlider()
                       }
                }
            }
        }
    }
}


@Preview(showBackground = true , showSystemUi = true)
@Composable
fun MusicSlider(){
    Box(contentAlignment = Alignment.Center , modifier = Modifier.fillMaxSize()){
        CircularSlider(
            modifier = Modifier.size(275.dp),
            stroke = 25f,
            progressColor = purpule,
            thumbColor = purpule,
            onChange = {}
        )
        Image(
            painter = painterResource(id = R.drawable.mellow),
            contentDescription = null ,
            modifier = Modifier
                .clip(shape = CircleShape)
                .size(200.dp),
            contentScale = ContentScale.Crop

        )


        Box(modifier = Modifier.height(275.dp) ,
            contentAlignment = Alignment.BottomCenter){
            IconButton(
                onClick = { /*TODO*/ },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(color = purpule)
                    .size(55.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.PlayArrow,
                    contentDescription =  null ,
                    modifier = Modifier.size(35.dp),
                    tint = Color.White
                )
            }
        }
    }
}

