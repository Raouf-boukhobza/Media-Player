package com.raouf.audioplayer.data.local

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import androidx.annotation.WorkerThread
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class ContentResolverHelper @Inject constructor(
    @ApplicationContext  val context: Context
) {
    private var mCursor : Cursor? = null

    private val projection : Array<String> = arrayOf(
        MediaStore.Audio.AudioColumns.DISPLAY_NAME,
        MediaStore.Audio.AudioColumns._ID,
        MediaStore.Audio.AudioColumns.ARTIST,
        MediaStore.Audio.AudioColumns.DATA,
        MediaStore.Audio.AudioColumns.DURATION,
        MediaStore.Audio.AudioColumns.TITLE,
    )

    private val selectionclose  : String = "${MediaStore.Audio.AudioColumns.IS_MUSIC} = ?"
    private val selectionArg = arrayOf("1")
    private val sortdata = "${MediaStore.Audio.AudioColumns.DISPLAY_NAME} ASC"

    @WorkerThread
    fun getdata() : List<Audio>{
      return getcursordata()
    }


    private fun getcursordata() : MutableList<Audio>{
        var audiolist = mutableListOf<Audio>()
          mCursor = context.contentResolver.query(
              MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
              projection,
              selectionclose,
              selectionArg,
              sortdata
          )

        mCursor?.use {cursor ->
            val id = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns._ID)
            val displayname = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DISPLAY_NAME)
            val artist = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.ARTIST)
            val duration = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DURATION)
            val data =  cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.DATA)
            val title = cursor.getColumnIndexOrThrow(MediaStore.Audio.AudioColumns.TITLE)

           cursor.apply {
                if (count != 0){
                  while (cursor.moveToNext()){
                      val id = getLong(id)
                      val displayname = getString(displayname)
                      val duration = getInt(duration)
                      val title  = getString(title)
                      val artist = getString(artist)
                      val data = getString(data)

                      val uri = ContentUris.withAppendedId(
                          MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                          id
                      )

                      audiolist += Audio(
                          uri,
                          displayname,
                          id,
                          artist,
                          duration,
                          data,
                          title
                      )
                  }
                }
            }
        }
     return audiolist
    }

}