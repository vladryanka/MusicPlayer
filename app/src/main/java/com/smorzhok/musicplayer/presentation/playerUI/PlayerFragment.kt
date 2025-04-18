package com.smorzhok.musicplayer.presentation.playerUI

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.smorzhok.musicplayer.R
import com.smorzhok.musicplayer.data.remote.RepositoryProvider
import com.smorzhok.musicplayer.databinding.FragmentPlayerBinding
import com.smorzhok.musicplayer.domain.model.PlaybackState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch

class PlayerFragment : Fragment() {

    private var _binding: FragmentPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: PlayerViewModel by viewModels {
        PlayerViewModelFactory(RepositoryProvider.getPlayerRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val selectedIndex = PlayerFragmentArgs.fromBundle(requireArguments()).initialIndex
        viewModel.initWithTrackIndex(selectedIndex)
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorMessage
                    .filter { it.isNotBlank() }
                    .collectLatest { message ->
                        Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE)
                            .setAction(getString(R.string.retry)) {
                                viewModel.retry()
                            }
                            .show()
                    }
            }
        }


        lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    val track = state.track

                    if (track != null) {
                        binding.textViewTitle.text = track.title
                        binding.textViewArtist.text = track.artist
                        if (track.coverUrl == "") {
                            binding.imageViewCover.setImageResource(R.drawable.photo_placeholder)
                        } else
                            Glide.with(this@PlayerFragment)
                                .load(track.coverUrl)
                                .into(binding.imageViewCover)

                        if (state.playbackState == PlaybackState.PLAYING) {
                            binding.buttonPlayPause.setImageResource(R.drawable.pause)
                        } else {
                            binding.buttonPlayPause.setImageResource(R.drawable.play)
                        }

                        binding.seekBar.max = state.progress.duration
                        binding.seekBar.progress = state.progress.currentPosition
                        binding.textViewCurrentTime.text =
                            formatTime(state.progress.currentPosition)
                        binding.textViewDuration.text = formatTime(state.progress.duration)
                    }
                }
            }
        }

        binding.seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(sb: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) viewModel.seekTo(progress)
            }

            override fun onStartTrackingTouch(sb: SeekBar?) {}
            override fun onStopTrackingTouch(sb: SeekBar?) {}
        })

        binding.buttonPlayPause.setOnClickListener {
            val state = viewModel.uiState.value.playbackState
            if (state == PlaybackState.PLAYING) viewModel.pause() else viewModel.resume()
        }

        binding.buttonNext.setOnClickListener { viewModel.next() }
        binding.buttonPrevious.setOnClickListener { viewModel.previous() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @SuppressLint("DefaultLocale")
    private fun formatTime(seconds: Int): String {
        val mins = seconds / 60
        val secs = seconds % 60
        return String.format("%02d:%02d", mins, secs)
    }
}
