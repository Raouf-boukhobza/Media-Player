package com.raouf.audioplayer.data.local

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Audio(
    val uri : Uri,
    val name : String,
    val id : Long,
    val artist : String,
    val duration : Int,
    val data : String,
    val title : String
) : Parcelable
