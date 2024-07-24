package com.raouf.audioplayer.audio

import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.net.toUri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import com.raouf.audioplayer.data.local.Audio
import com.raouf.audioplayer.data.repository.AudioRepository
import com.raouf.audioplayer.player.JetAudioEvent
import com.raouf.audioplayer.player.JetAudioServiceHandler
import com.raouf.audioplayer.player.JetAudioState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import javax.inject.Inject


private val initialAudio : Audio = Audio(
    "".toUri(),
    "",
    0L,
    "",
    0,
    "",
    ""
)
@OptIn(SavedStateHandleSaveableApi::class)
@HiltViewModel
class AudioViewModel @Inject constructor(
    private val audioServiceHandler : JetAudioServiceHandler,
    private val repository: AudioRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    var duration by savedStateHandle.saveable { mutableLongStateOf(0L) }
    var progress by savedStateHandle.saveable { mutableFloatStateOf(0f) }
    var progressString by savedStateHandle.saveable { mutableStateOf("00:00") }
    var isPlaying by savedStateHandle.saveable { mutableStateOf(false) }
    var currentSelectedAudio by savedStateHandle.saveable { mutableStateOf(initialAudio) }
    var audioList by savedStateHandle.saveable { mutableStateOf(listOf<Audio>()) }


    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Initial)
    var uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loudAudioData()
    }
    init {
        viewModelScope.launch{
            audioServiceHandler.audioState.collectLatest {mediaState ->
                when (mediaState) {
                     JetAudioState.Initial -> _uiState.value = UiState.Initial
                    is JetAudioState.Buferring ->  calculateprogress(mediaState.progress)
                    is JetAudioState.Playing -> isPlaying = mediaState.isPlaying
                    is JetAudioState.CurrentPlaying -> {
                        currentSelectedAudio = audioList[mediaState.mediaitemIndex]
                    }
                    is JetAudioState.Progress -> calculateprogress(mediaState.progress)
                    is JetAudioState.Ready -> {
                        duration = mediaState.duration
                        _uiState.value = UiState.Ready
                    }
                }
            }
        }
    }

    private fun loudAudioData(){
        viewModelScope.launch{
            val audio = repository.getaudio()
            audioList = audio
            setMediaItem()
        }
    }


    private fun setMediaItem(){
        audioList.map {audio ->
            androidx.media3.common.MediaItem.Builder()
                .setUri(audio.uri)
                .setMediaMetadata(
                    androidx.media3.common.MediaMetadata.Builder()
                        .setArtist(audio.artist)
                        .setTitle(audio.title)
                        .setSubtitle(audio.name)
                        .build()
                )
                .build()
        }.also {
            audioServiceHandler.addMediaItems(it)
        }
    }



    private fun onUiEvents(uievent : UiEvents) = viewModelScope.launch {
        when(uievent){
            is UiEvents.BackWard -> audioServiceHandler.onPlayerEvent(JetAudioEvent.BackWard)
            is UiEvents.Forward -> audioServiceHandler.onPlayerEvent(JetAudioEvent.Forward)
            is UiEvents.PlayPause -> audioServiceHandler.onPlayerEvent(JetAudioEvent.PlayPause)
            is UiEvents.SeekToNext -> audioServiceHandler.onPlayerEvent(JetAudioEvent.SeekToNext)
            is UiEvents.SeekTo -> {
                audioServiceHandler.onPlayerEvent(JetAudioEvent.SeekTo,
                    seektoposition = ((duration * uievent.position ) /100F).toLong()
                )
            }

            is UiEvents.SelectedAudioChange ->{
                audioServiceHandler.onPlayerEvent(
                    JetAudioEvent.SelectedAudioChange,
                    selectedmediaindex = uievent.index
                )
            }
            is UiEvents.UpdateProgress -> {
                audioServiceHandler.onPlayerEvent(
                    JetAudioEvent.UpdateProgress(
                        uievent.newProgress
                    )

                )
            }

        }
    }


    private fun calculateprogress(currentProgress: Long) {
        progress =
            if (currentProgress > 0)
                ((currentProgress.toFloat() / duration.toFloat()) * 100f)
            else 0F

        progressString = formatProgressString(currentProgress)
    }

    private fun formatProgressString(duration : Long) : String{
        val minute = TimeUnit.MINUTES.convert(duration , TimeUnit.MILLISECONDS)
        val second = (minute) - TimeUnit.SECONDS.convert(1, TimeUnit.MINUTES)
        return String.format("%02d:%02d" , minute,second)
    }

}


sealed class UiEvents{
    data object PlayPause : UiEvents()
    data class SelectedAudioChange(val index : Int) : UiEvents()
    data object Forward : UiEvents()
    data object SeekToNext : UiEvents()
    data object BackWard : UiEvents()
    data class SeekTo(val position: Float) : UiEvents()
    data object Stop : UiEvents()
    data class UpdateProgress(val newProgress : Float) : UiEvents()
}

sealed class UiState{
    data object Initial : UiState()
    data object Ready : UiState()
}