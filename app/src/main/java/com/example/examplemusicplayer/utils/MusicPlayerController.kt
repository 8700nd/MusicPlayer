package com.example.examplemusicplayer.utils

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import com.example.examplemusicplayer.data.models.Song
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import timber.log.Timber
import java.io.IOException
import kotlin.random.Random

class MediaPlayerController {
    enum class PlaybackMode {
        REPEAT_ALL, REPEAT_ONE, SHUFFLE
    }

    var currentMode = PlaybackMode.REPEAT_ALL
    var library: List<Song> = mutableListOf()
    private var currentIndex = 0
    private var mediaPlayer: MediaPlayer? = null
    private var pausedPosition: Int = 0
    private val handler = Handler(Looper.getMainLooper())
    private val updateSeekBarTask = object : Runnable {
        override fun run() {
            mediaPlayer?.let {
                _playBackPositionStateFlow.value = it.currentPosition
                Timber.d("position = ${_playBackPositionStateFlow.value}")
                handler.postDelayed(this, 1000)
            }
        }
    }

    private val _currentPlayingSongStateFlow = MutableStateFlow<Song?>(null)
    val currentPlayingSongStateFlow = _currentPlayingSongStateFlow.asStateFlow()

    private val _playbackModeStateFlow = MutableStateFlow<PlaybackMode>(PlaybackMode.REPEAT_ALL)
    val playbackModeStateFlow = _playbackModeStateFlow.asStateFlow()

    private val _isPausedStateFlow = MutableStateFlow<Boolean?>(null)
    val isPausedStateFlow = _isPausedStateFlow.asStateFlow()

    private val _playBackPositionStateFlow = MutableStateFlow(0)
    val playbackPositionStateFlow = _playBackPositionStateFlow.asStateFlow()

    init {
        mediaPlayer = MediaPlayer().apply {
            setOnCompletionListener { songFinished() }
        }
    }

    fun updateLibrary(library: List<Song>) {
        this.library = library
    }

    fun pause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
            pausedPosition = mediaPlayer?.currentPosition ?: 0
            _isPausedStateFlow.value = true
            handler.removeCallbacks(updateSeekBarTask)
            Timber.d("Paused: ${currentSong().title} at position $pausedPosition")
        }
    }

    fun playSpecificSong(song: Song, index: Int) {
        currentIndex = index
        _currentPlayingSongStateFlow.value = song
        mediaPlayer?.apply {
            reset()
            try {
                setDataSource(song.filePath)
                prepare()
                start()
                _isPausedStateFlow.value = false
                pausedPosition = 0
                handler.post(updateSeekBarTask)
                Timber.d("Playing: ${song.title} by ${song.artist}")
            } catch (e: IOException) {
                Timber.e("Error setting data source", e)
            }
        }
    }

    // play song
    fun start() {
        val song = currentSong()
        _currentPlayingSongStateFlow.value = currentSong()
        mediaPlayer?.apply {
            reset()
            try {
                setDataSource(song.filePath)
                prepare()
                start()
                _isPausedStateFlow.value = false
                pausedPosition = 0
                handler.post(updateSeekBarTask)
                Timber.d("Playing: ${song.title} by ${song.artist}")
            } catch (e: IOException) {
                Timber.e("Error setting data source", e)
            }
        }
    }

    // if already playing, resume
    fun resume() {
        mediaPlayer?.apply {
            seekTo(pausedPosition)
            start()
            _isPausedStateFlow.value = false
            handler.post(updateSeekBarTask)
            Timber.d("Resumed: ${currentSong().title} from position $pausedPosition")
        }
    }

    fun next() {
        if (library.isEmpty()) return
        currentIndex = if (currentMode == PlaybackMode.SHUFFLE) {
            Random.nextInt(library.size)
        } else {
            (currentIndex + 1) % library.size
        }
        Timber.d("Next song index = $currentIndex")
        start()
    }

    fun previous() {
        if (library.isEmpty()) return
        currentIndex = if (currentMode == PlaybackMode.SHUFFLE) {
            Random.nextInt(library.size)
        } else {
            if (currentIndex - 1 < 0) library.size - 1 else currentIndex - 1
        }
        Timber.d("Previous song index = $currentIndex")
        start()
    }

    fun setPlaybackMode(mode: PlaybackMode) {
        currentMode = mode
        _playbackModeStateFlow.value = mode
        Timber.d("Playback mode set to: $mode")
    }

    fun isPlaying(): Boolean = mediaPlayer?.isPlaying ?: false

    fun currentSong(): Song = library.getOrNull(currentIndex) ?: Song("", "", "")

    fun isPaused(): Boolean? = isPausedStateFlow.value

    private fun songFinished() {
        when (currentMode) {
            PlaybackMode.SHUFFLE, PlaybackMode.REPEAT_ALL -> next()
            PlaybackMode.REPEAT_ONE -> start()
        }
    }

    fun setPlaybackPosition(position: Int) {
        mediaPlayer?.seekTo(position)
    }

    fun getSongDuration(): Int = mediaPlayer?.duration ?: 0

    fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
        Timber.d("Media Player released")
    }

}