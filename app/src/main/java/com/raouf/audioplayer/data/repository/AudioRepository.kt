package com.raouf.audioplayer.data.repository

import com.raouf.audioplayer.data.local.Audio
import com.raouf.audioplayer.data.local.ContentResolverHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AudioRepository @Inject constructor(
    private var contentResolver: ContentResolverHelper
){
     suspend fun getaudio() : List<Audio> = withContext(Dispatchers.IO){
         contentResolver.getdata()
     }
}