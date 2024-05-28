package com.example.examplemusicplayer.utils

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import com.example.examplemusicplayer.data.models.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

val MEDIAPERMISSIONS = arrayOf(
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.READ_MEDIA_AUDIO
)

class ScanMediaUtil(private val context: Context) {

    suspend fun fetchSongsFromDevice(): List<Song> = withContext(Dispatchers.IO) {
        val songs = mutableListOf<Song>()
        val contentResolver: ContentResolver = context.contentResolver
        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"
        val cursor = contentResolver.query(uri, projection, selection, null, sortOrder)

        if (cursor != null) {
            Timber.d("Cursor not null, count: ${cursor.count}")
            while (cursor.moveToNext()) {
                val filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                val artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                Timber.d("Found song: $title by $artist at $filePath")
                val song = Song(filePath, title, artist)
                songs.add(song)
            }
            cursor.close()
        } else {
            Timber.d("Cursor is null")
        }

        Timber.d("songs = $songs")
        return@withContext songs
    }
}