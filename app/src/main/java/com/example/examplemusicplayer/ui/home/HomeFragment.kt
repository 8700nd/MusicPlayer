package com.example.examplemusicplayer.ui.home

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_AUDIO
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.examplemusicplayer.R
import com.example.examplemusicplayer.utils.MediaPlayerController
import com.example.examplemusicplayer.databinding.FragmentHomeBinding
import com.example.examplemusicplayer.ui.Event
import com.example.examplemusicplayer.ui.MediaEvents
import com.example.examplemusicplayer.utils.MEDIAPERMISSIONS
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var rvAdapter: HomeRecyclerViewAdapter

    private val homeViewModel: HomeViewModel by viewModel()
    private lateinit var playlistBottomSheetBehavior: BottomSheetBehavior<View>

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            homeViewModel.scanSongs()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                homeViewModel.events.collect(::observeEvents)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setListeners()
        initRecyclerView()
        observePlaylistData()
        observeCurrentPlayingSong()
        observePlayPauseState()
        observePlaybackModeState()
        setSeekBar()
        observePlaybackPositionState()
        if (MEDIAPERMISSIONS.any { hasPermission(requireContext(), it) }) {
            homeViewModel.scanSongs()
        } else {
            requestPermission()
        }
    }

    private fun observeEvents(event: Event) {
        when (event) {
            is MediaEvents.EmptyLibrary -> {
                showToast("No media found")
            }
        }
    }

    private fun requestPermission() {
        when {
            shouldShowRequestPermissionRationale(READ_MEDIA_AUDIO) ||
                    shouldShowRequestPermissionRationale(READ_EXTERNAL_STORAGE)
            -> {
                showToast("Permission not given")
            }

            else -> {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    requestPermissionLauncher.launch(READ_MEDIA_AUDIO)
                } else {
                    requestPermissionLauncher.launch(READ_EXTERNAL_STORAGE)
                }
            }
        }
    }

    private fun setSeekBar() {
        binding.playbackSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    homeViewModel.mediaPlayerController.setPlaybackPosition(progress * 1000)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })
    }

    private fun setListeners() {
        playlistBottomSheetBehavior = BottomSheetBehavior.from(binding.playlistBottomSheet.root)

        binding.nextButton.setOnClickListener {
            homeViewModel.next()
        }
        binding.previousButton.setOnClickListener {
            homeViewModel.previous()
        }
        binding.playList.setOnClickListener {
            playlistBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        binding.playPauseButton.setOnClickListener {
            if (homeViewModel.mediaPlayerController.isPlaying()) {
                homeViewModel.pause()
            } else {
                homeViewModel.play()
            }
        }

        binding.modeButton.setOnClickListener {
            when (homeViewModel.mediaPlayerController.currentMode) {
                MediaPlayerController.PlaybackMode.SHUFFLE -> {
                    homeViewModel.setPlaybackMode(MediaPlayerController.PlaybackMode.REPEAT_ALL)
                }

                MediaPlayerController.PlaybackMode.REPEAT_ALL -> {
                    homeViewModel.setPlaybackMode(MediaPlayerController.PlaybackMode.REPEAT_ONE)
                }

                MediaPlayerController.PlaybackMode.REPEAT_ONE -> {
                    homeViewModel.setPlaybackMode(MediaPlayerController.PlaybackMode.SHUFFLE)
                }
            }
        }
    }

    private fun changePlaybackModeUi(mode: MediaPlayerController.PlaybackMode) {
        when (mode) {
            MediaPlayerController.PlaybackMode.SHUFFLE -> {
                Timber.d("Setting mode to Shuffle")
                binding.modeButton.setImageResource(R.drawable.ic_shuffle)
            }

            MediaPlayerController.PlaybackMode.REPEAT_ALL -> {
                Timber.d("Setting mode to Repeat All")
                binding.modeButton.setImageResource(R.drawable.ic_repeatall)
            }

            MediaPlayerController.PlaybackMode.REPEAT_ONE -> {
                Timber.d("Setting mode to Repeat One")
                binding.modeButton.setImageResource(R.drawable.ic_repeatone)
            }
        }
    }

    private fun changePlayPauseUi(showPlay: Boolean) {
        when (showPlay) {
            true -> {
                binding.playPauseButton.setImageResource(R.drawable.ic_play)
            }

            false -> {
                binding.playPauseButton.setImageResource(R.drawable.ic_pause)
            }
        }
    }

    private fun observePlaylistData() {
        lifecycleScope.launch(Dispatchers.Main) {
            homeViewModel.songListStateFlow.collect {songList ->
                songList?.let {
                    rvAdapter.updateData(it)
                }
            }
        }
    }

    private fun observeCurrentPlayingSong() {
        lifecycleScope.launch(Dispatchers.Main) {
            homeViewModel.mediaPlayerController.currentPlayingSongStateFlow.collect {song ->
                if (song != null) {
                    binding.currentSongTextView.text = "${song.title} \n ${song.artist}"
                    binding.playbackSeekBar.max = homeViewModel.mediaPlayerController.getSongDuration() / 1000
                }
            }
        }
    }

    private fun observePlayPauseState() {
        lifecycleScope.launch(Dispatchers.Main) {
            homeViewModel.mediaPlayerController.isPausedStateFlow.collect { isPaused ->
                if (isPaused != null) {
                    changePlayPauseUi(isPaused)
                }
            }
        }
    }

    private fun observePlaybackModeState() {
        lifecycleScope.launch(Dispatchers.Main) {
            homeViewModel.mediaPlayerController.playbackModeStateFlow.collect { mode ->
                changePlaybackModeUi(mode)
            }
        }
    }

    private fun observePlaybackPositionState() {
        lifecycleScope.launch(Dispatchers.Main) {
            homeViewModel.mediaPlayerController.playbackPositionStateFlow.collect {
                Timber.d("observePlaybackPositionState, playbackPositionStateFlow.collect $it")
                binding.playbackSeekBar.progress = it / 1000
            }
        }
    }

    private fun initRecyclerView() {
        binding.playlistBottomSheet.recyclerView.adapter = getRvAdapter()
        binding.playlistBottomSheet.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        rvAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
    }

    private fun getRvAdapter(): HomeRecyclerViewAdapter {
        return HomeRecyclerViewAdapter(
            onUserClicked = {
                song, index ->
                homeViewModel.playSpecificSong(song, index)
            }
        ).also {
            rvAdapter = it
        }
    }

    private fun hasPermission(context: Context, permissionType: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permissionType
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onDestroy() {
        super.onDestroy()
        homeViewModel.mediaPlayerController.releaseMediaPlayer()
    }

}