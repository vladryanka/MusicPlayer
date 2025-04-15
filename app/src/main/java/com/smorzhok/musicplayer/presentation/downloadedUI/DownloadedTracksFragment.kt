package com.smorzhok.musicplayer.presentation.downloadedUI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.smorzhok.musicplayer.data.remote.RepositoryProvider
import com.smorzhok.musicplayer.databinding.FragmentDownloadedTracksBinding
import kotlinx.coroutines.launch

class DownloadedTracksFragment : Fragment() {
    private var _binding: FragmentDownloadedTracksBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: DownloadedTracksAdapter
    private val viewModel: DownloadedTracksViewModel by viewModels {
        DownloadedTracksViewModelFactory(RepositoryProvider.getTrackRepository())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDownloadedTracksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = DownloadedTracksAdapter { track ->
            /*val action = DownloadedTracksFragmentDirections
                .actionDownloadedTracksFragmentToPlayerFragment(track.id)
            findNavController().navigate(action)*/
        }

        binding.recyclerViewDownloadedTracks.adapter = adapter
        binding.recyclerViewDownloadedTracks.layoutManager = LinearLayoutManager(requireContext())

        binding.searchEditText.addTextChangedListener { editable ->
            val query = editable.toString()
            viewModel.searchTracks(query)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.tracks.collect { tracks ->
                adapter.submitList(tracks)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}