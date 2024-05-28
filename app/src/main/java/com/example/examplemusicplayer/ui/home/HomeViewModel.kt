package com.example.examplemusicplayer.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.examplemusicplayer.utils.MediaPlayerController
import com.example.examplemusicplayer.data.models.Song
import com.example.examplemusicplayer.ui.Event
import com.example.examplemusicplayer.ui.MediaEvents
import com.example.examplemusicplayer.utils.ScanMediaUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber


class HomeViewModel(
    val mediaPlayerController: MediaPlayerController,
    private val scanMediaUtil: ScanMediaUtil
) : ViewModel() {
    private val _events = MutableSharedFlow<Event>()
    val events: SharedFlow<Event> = _events.asSharedFlow()

    private val _songListStateFlow = MutableStateFlow<List<Song>?>(null)
    val songListStateFlow: StateFlow<List<Song>?> = _songListStateFlow.asStateFlow()

    fun scanSongs() {
        Timber.d("scanSongs")
        viewModelScope.launch(Dispatchers.IO) {
            val list = scanMediaUtil.fetchSongsFromDevice()
            mediaPlayerController.updateLibrary(list)
            _songListStateFlow.value = list
        }
    }

    fun play() {
        if (mediaPlayerController.library.isEmpty()) {
            MediaEvents.EmptyLibrary.emit()
            return
        }
        if (mediaPlayerController.isPaused() == null || mediaPlayerController.isPaused() == false) {
            // If isPaused is at initial state or is not paused, play the song
            mediaPlayerController.start()
        } else {
            // If isPaused, resume
            mediaPlayerController.resume()
        }
    }

    fun playSpecificSong(song: Song, index: Int) {
        if (mediaPlayerController.library.isEmpty()) {
            MediaEvents.EmptyLibrary.emit()
            return
        }
        mediaPlayerController.playSpecificSong(song, index)
    }

    fun pause() {
        if (mediaPlayerController.isPlaying()) {
            mediaPlayerController.pause()
        }
    }

    fun next() {
        if (mediaPlayerController.library.isEmpty()) {
            MediaEvents.EmptyLibrary.emit()
            return
        }
        mediaPlayerController.next()
    }

    fun previous() {
        if (mediaPlayerController.library.isEmpty()) {
            MediaEvents.EmptyLibrary.emit()
            return
        }
        mediaPlayerController.previous()
    }

    fun setPlaybackMode(mode: MediaPlayerController.PlaybackMode) {
        mediaPlayerController.setPlaybackMode(mode)
    }

    private fun Event.emit() {
        viewModelScope.launch {
            _events.emit(this@emit)
        }
    }
}