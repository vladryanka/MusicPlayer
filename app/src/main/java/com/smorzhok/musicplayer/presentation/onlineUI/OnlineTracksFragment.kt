package com.smorzhok.musicplayer.presentation.onlineUI

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.smorzhok.musicplayer.R
import com.smorzhok.musicplayer.data.remote.RepositoryProvider
import com.smorzhok.musicplayer.databinding.FragmentDownloadedTracksBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class OnlineTracksFragment : Fragment() {
    private var _binding: FragmentDownloadedTracksBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: OnlineTracksAdapter
    private var lastQuery: String? = null

    private val viewModel: OnlineTracksViewModel by viewModels {
        OnlineTracksViewModelFactory(RepositoryProvider.getTrackRepository())
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

        binding.textViewHeader.text = getString(R.string.search_from_internet)

        adapter = OnlineTracksAdapter { track ->
            /*
            val action = OnlineTracksFragmentDirections
                .actionNetworkTracksFragmentToPlayerFragment(track.id)
            findNavController().navigate(action)*/
        }

        binding.recyclerViewDownloadedTracks.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewDownloadedTracks.adapter = adapter

        binding.searchEditText.addTextChangedListener { editable ->
            val query = editable.toString()
            lastQuery = query
            binding.progressBarPagination.visibility = View.VISIBLE
            viewModel.searchTracks(query)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.errorMessage.collectLatest { message ->
                    Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE)
                        .setAction(getString(R.string.retry)) {
                            lastQuery?.let {
                                viewModel.searchTracks(it)
                            } ?: viewModel.loadDefaultTracks()
                        }.show()
                }
            }
        }

        binding.recyclerViewDownloadedTracks.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()

                val isLastItemVisible = lastVisibleItemPosition >= totalItemCount - 2
                if (isLastItemVisible) {
                    viewModel.loadMoreTracks()
                }
            }
        })

        binding.searchEditText.addTextChangedListener { editable ->
            val query = editable.toString()
            binding.progressBarPagination.visibility = View.VISIBLE
            viewModel.searchTracks(query)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.tracks.collectLatest {
                    adapter.submitList(it)
                    binding.progressBarPagination.visibility = View.GONE
                }
            }
        }

        viewModel.loadDefaultTracks()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}