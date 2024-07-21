package com.raouf.audioplayer.player.notification


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.ui.PlayerNotificationManager
import com.raouf.audioplayer.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject


private val Notification_Id = 101
private val Notification_Channel_name = "notification channel 1"
private val Notification_Channel_id = "notification id 1"



class Jetaudionotificationmanager @Inject constructor(
    @ApplicationContext private val context: Context,
    private var exoPlayer: ExoPlayer
){

    init {
        createnotificationchannel()
    }


    private fun Startnotificationservice(
        mediaSessionService: MediaSessionService,
        mediaSession: MediaSession
    ){
        buildnotification(mediaSession)
    }

    private fun forgroundsesrvicemanager(mediasessionservice : MediaSessionService){
        val notification = Notification.Builder(context , Notification_Channel_id)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
            mediasessionservice.startForeground(Notification_Id,notification)
    }

    @OptIn(androidx.media3.common.util.UnstableApi::class)
    private fun buildnotification(mediaSession: androidx.media3.session.MediaSession){
        mediaSession.sessionActivity?.let {
            JetAudioNotificationAdapter(
                context = context,
                pendingIntent = it
            )
        }?.let { it ->
            PlayerNotificationManager.Builder(
                context,
                Notification_Id,
                Notification_Channel_id
            )
                .setMediaDescriptionAdapter(it)
                .setSmallIconResourceId(R.drawable.music_note_24)
                .build()
                .also {
                    it.setUseFastForwardActionInCompactView(true)
                    it.setMediaSessionToken(mediaSession.sessionCompatToken)
                    it.setUseRewindActionInCompactView(true)
                    it.setUseNextActionInCompactView(true)
                    it.setPriority(NotificationCompat.PRIORITY_LOW)
                    it.setPlayer(exoPlayer)
                }


        }
    }




    private val notificaitonmanager : NotificationManagerCompat =
        NotificationManagerCompat.from(context)

    private fun createnotificationchannel(){
        val channel = NotificationChannel(
            Notification_Channel_id,
            Notification_Channel_name,
            NotificationManager.IMPORTANCE_LOW
        )
       notificaitonmanager.createNotificationChannel(channel)
    }
}