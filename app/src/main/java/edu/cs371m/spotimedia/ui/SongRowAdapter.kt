package edu.cs371m.spotimedia.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.cs371m.spotimedia.R
import edu.cs371m.spotimedia.api.Album
import edu.cs371m.spotimedia.api.Track
import edu.cs371m.spotimedia.databinding.RowSongBinding
import edu.cs371m.spotimedia.glide.Glide

class SongRowAdapter(private val viewModel: MainViewModel,
                        private val navigateToAlbum: (Album) -> Unit) :
    ListAdapter<Track, SongRowAdapter.VH>(TrackDiff()) {

    class TrackDiff : DiffUtil.ItemCallback<Track>() {
        override fun areItemsTheSame(oldItem: Track, newItem: Track): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Track,
            newItem: Track
        ): Boolean {
            return oldItem.name == newItem.name &&
                    oldItem.album == newItem.album &&
                    oldItem.popularity == newItem.popularity
        }
    }

    inner class VH(val rowBinding: RowSongBinding) : RecyclerView.ViewHolder(rowBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SongRowAdapter.VH {
        val rowBinding = RowSongBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        return VH(rowBinding)
    }
    override fun onBindViewHolder(holder: SongRowAdapter.VH, position: Int) {
        val track = getItem(position)
        holder.rowBinding.album.text = track.album.name
        holder.rowBinding.songName.text = track.name
        if (track.artists.isNotEmpty()) holder.rowBinding.artists.text = track.artists[0].name
        if (track.album.images.isNotEmpty()) Glide.glideFetch(track.album.images[0].url, track.album.images[0].url, holder.rowBinding.songPic)
        else holder.rowBinding.songPic.setBackgroundResource(R.drawable.ic_cloud_download_black_24dp)

        holder.rowBinding.root.setOnClickListener { navigateToAlbum(track.album) }
    }
}