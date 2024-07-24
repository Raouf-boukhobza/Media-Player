package com.raouf.audioplayer.player

import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class JetAudioServiceHandler @Inject constructor(
  private val exoPlayer: ExoPlayer
) : Player.Listener {
   private val _audioState : MutableStateFlow<JetAudioState> =
       MutableStateFlow(JetAudioState.Initial)
    val audioState : StateFlow<JetAudioState> = _audioState.asStateFlow()

    private var job : Job? = null
    override fun onPlaybackStateChanged(playbackState: Int){
        when(playbackState){
            ExoPlayer.STATE_BUFFERING -> _audioState.value = JetAudioState.Buferring(exoPlayer.currentPosition)
            ExoPlayer.STATE_READY -> _audioState.value = JetAudioState.Ready(exoPlayer.duration)
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        _audioState.value = JetAudioState.Playing(isPlaying = isPlaying)
        _audioState.value = JetAudioState.CurrentPlaying(exoPlayer.currentMediaItemIndex)
        if (isPlaying){
            GlobalScope.launch(Dispatchers.IO) {
                startProgressUpdate()
            }
        }else{
            stopProgressUpdate()
        }

    }

     fun addMediaItem(mediaitem : MediaItem){
      exoPlayer.addMediaItem(mediaitem)
     }

    fun addMediaItems(mediaitems:List<MediaItem>){
        exoPlayer.addMediaItems(mediaitems)
    }

    suspend fun onPlayerEvent(
        jetAudioEvent : JetAudioEvent,
        selectedmediaindex : Int = -1,
        seektoposition : Long = 0
    ){
        when(jetAudioEvent){
            JetAudioEvent.BackWard -> exoPlayer.seekBack()
            JetAudioEvent.Forward -> exoPlayer.seekForward()
            JetAudioEvent.SeekToNext -> exoPlayer.seekToNext()
            JetAudioEvent.PlayPause -> PlayOrPause()
            JetAudioEvent.SeekTo -> exoPlayer.seekTo(seektoposition)
            JetAudioEvent.SelectedAudioChange ->{
                when(selectedmediaindex){
                 exoPlayer.currentMediaItemIndex -> {
                     PlayOrPause()
                 }else -> {
                     exoPlayer.seekToDefaultPosition(selectedmediaindex)
                    _audioState.value = JetAudioState.Playing(
                        isPlaying = true
                    )
                    exoPlayer.playWhenReady = true
                    startProgressUpdate()
                 }
                }
            }
            JetAudioEvent.Stop -> stopProgressUpdate()
            is  JetAudioEvent.UpdateProgress -> {
                exoPlayer.seekTo(
                    (exoPlayer.duration * jetAudioEvent.newProgress ))
            }
        }
    }



    private suspend fun PlayOrPause(){
        if (exoPlayer.isPlaying){
            exoPlayer.pause()
            stopProgressUpdate()

        }else{
            exoPlayer.play()
            _audioState.value = JetAudioState.Playing(
                isPlaying = true
            )
            startProgressUpdate()
        }
    }


    //updata the progress when the audio is playing
    private suspend fun startProgressUpdate() = job.run {
        while (true){
            delay(500)
            _audioState.value = JetAudioState.Progress(exoPlayer.currentPosition)
        }
    }

    //change the state when the audio is paused
    private fun stopProgressUpdate() = job.run {
        job?.cancel()
        _audioState.value = JetAudioState.Playing(
            isPlaying = false
        )
    }
}






sealed class JetAudioEvent{
    data object PlayPause : JetAudioEvent()
    data object SelectedAudioChange : JetAudioEvent()
    data object Forward : JetAudioEvent()
    data object SeekToNext : JetAudioEvent()
    data object BackWard : JetAudioEvent()
    data object SeekTo : JetAudioEvent()
    data object Stop : JetAudioEvent()
    data class UpdateProgress(val newProgress : Long) : JetAudioEvent()
}


sealed class JetAudioState{
    data object Initial : JetAudioState()
    data class Ready(val duration: Long) : JetAudioState()
    data class Buferring(val progress : Long) : JetAudioState()
    data class Progress(val progress: Long) : JetAudioState()
    data class Playing (val isPlaying : Boolean) : JetAudioState()
    data class CurrentPlaying(val mediaitemIndex : Int) : JetAudioState()
}