package edu.cs371m.spotimedia.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.cs371m.spotimedia.R
import edu.cs371m.spotimedia.api.Artist
import edu.cs371m.spotimedia.databinding.RowArtistBinding
import edu.cs371m.spotimedia.glide.Glide

class ArtistRowAdapter(private val viewModel: MainViewModel,
                        private val navigateToArtist: (Artist) -> Unit) :
    ListAdapter<Artist, ArtistRowAdapter.VH>(ArtistDiff()) {

    class ArtistDiff : DiffUtil.ItemCallback<Artist>() {
        override fun areItemsTheSame(oldItem: Artist, newItem: Artist): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: Artist,
            newItem: Artist
        ): Boolean {
            return oldItem.genres.containsAll(newItem.genres) &&
                    oldItem.name == newItem.name &&
                    oldItem.followers == newItem.followers &&
                    oldItem.popularity == newItem.popularity
        }
    }

    inner class VH(val rowBinding: RowArtistBinding) : RecyclerView.ViewHolder(rowBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistRowAdapter.VH {
        val rowBinding = RowArtistBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        return VH(rowBinding)
    }

    override fun onBindViewHolder(holder: ArtistRowAdapter.VH, position: Int) {
        val artist = getItem(position)
        holder.rowBinding.artistName.text = artist.name
        holder.rowBinding.followerCount.text = "Followers: ${artist.followers.total}"

        holder.rowBinding.genres.text = if (artist.genres.size > 1)
            {"Genre: ${artist.genres.toString().substring(1, artist.genres.toString().length - 1)}"}
            else {"Genres: ${artist.genres.toString().substring(1, artist.genres.toString().length - 1)}"}

        if(artist.images.isNotEmpty()) Glide.glideFetch(artist.images[0].url, artist.images[0].url, holder.rowBinding.artistPic)
        else holder.rowBinding.artistPic.setBackgroundResource(R.drawable.ic_cloud_download_black_24dp)
        holder.rowBinding.root.setOnClickListener {
            navigateToArtist(artist)
        }
    }
}