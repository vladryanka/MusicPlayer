package com.smorzhok.musicplayer.presentation.downloadedUI

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_AUDIO
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.smorzhok.musicplayer.R
import com.smorzhok.musicplayer.data.remote.RepositoryProvider
import com.smorzhok.musicplayer.databinding.FragmentDownloadedTracksBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class DownloadedTracksFragment : Fragment() {
    private var _binding: FragmentDownloadedTracksBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: DownloadedTracksAdapter
    private val viewModel: DownloadedTracksViewModel by viewModels {
        DownloadedTracksViewModelFactory(RepositoryProvider.getTrackRepository())
    }

    override fun onResume() {
        super.onResume()
        if (hasMusicPermission()) {
            viewModel.loadDownloadedTracks()
        } else {
            Toast.makeText(requireContext(), R.string.permissions_arent_granted, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDownloadedTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun hasMusicPermission(): Boolean {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            READ_MEDIA_AUDIO
        } else {
            READ_EXTERNAL_STORAGE
        }
        return ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = DownloadedTracksAdapter { /*track ->
            val action = DownloadedTracksFragmentDirections
                .actionDownloadedTracksFragmentToPlayerFragment(track)
            findNavController().navigate(action)*/
        }

        binding.recyclerViewDownloadedTracks.adapter = adapter
        binding.recyclerViewDownloadedTracks.layoutManager = LinearLayoutManager(requireContext())

        binding.searchEditText.addTextChangedListener { editable ->
            val query = editable.toString()
            viewModel.searchTracks(query)
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tracks.collectLatest { tracks ->
                    adapter.submitList(tracks)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}