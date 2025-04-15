package com.smorzhok.musicplayer.presentation.downloadedUI

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smorzhok.musicplayer.databinding.ItemDownloadedTrackBinding
import com.smorzhok.musicplayer.domain.model.Track

class DownloadedTracksAdapter (
    private val onTrackClick: (Track) -> Unit
) : ListAdapter<Track, DownloadedTracksAdapter.TrackViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrackViewHolder {
        val binding = ItemDownloadedTrackBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrackViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrackViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class TrackViewHolder(private val binding: ItemDownloadedTrackBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(track: Track) {
            binding.textViewTitle.text = track.title
            binding.textViewArtist.text = track.artist
            Glide.with(binding.imageViewCover.context)
                .load(track.coverUrl)
                .into(binding.imageViewCover)

            binding.root.setOnClickListener {
                onTrackClick(track)
            }
        }
    }

    companion object DiffCallback : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: Track, newItem: Track): Boolean = oldItem == newItem
    }
}