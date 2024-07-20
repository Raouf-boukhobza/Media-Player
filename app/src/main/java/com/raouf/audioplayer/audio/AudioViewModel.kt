package com.raouf.audioplayer.audio

import android.media.JetPlayer
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import java.lang.reflect.Constructor
import javax.inject.Inject


@HiltViewModel
class AudioViewModel @Inject constructor(
    audioservicehandler : JetPlayer
) : ViewModel() {


}