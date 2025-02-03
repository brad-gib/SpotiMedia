package edu.cs371m.spotimedia.ui

import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Genres
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import edu.cs371m.spotimedia.R
import edu.cs371m.spotimedia.api.Artist
import edu.cs371m.spotimedia.api.Followers
import edu.cs371m.spotimedia.api.Image
import edu.cs371m.spotimedia.api.Track
import edu.cs371m.spotimedia.databinding.FragmentAlbumBinding
import edu.cs371m.spotimedia.glide.Glide
import okhttp3.internal.immutableListOf

class AlbumFragment: Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentAlbumBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private val args: AlbumFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAlbumBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")
        val album = args.post
        binding.albumName.text = album.name
        if(args.post.images.isNotEmpty()) Glide.glideFetch(args.post.images[0].url, args.post.images[0].url, binding.albumPic)
        else binding.albumPic.setBackgroundResource(R.drawable.ic_cloud_download_black_24dp)
        if (album.artists.size > 1) {"Artists: ${album.artists.toString().substring(1, album.artists.toString().length - 1)}"}
        else {"Artists: ${album.artists.toString().substring(1, album.artists.toString().length - 1)}"}
        viewModel.observeAlbumTracks().observeForever {
            val songRowAdapter = SongRowAdapter(viewModel) {}
            val tracks = mutableListOf<Track>()
            for (song in viewModel.observeAlbumTracks().value!!.items) {
                val list = immutableListOf<String>()
                val list2 = immutableListOf<Image>()
                val arts = mutableListOf<Artist>()
                Artist("", "", 0, list, list2, Followers(0))
                for (artist in album.artists) {
                    arts.add(Artist(artist.id, artist.name, 0, list, list2, Followers(0)))
                }
                val track = Track("", song.name, album, arts, 0)
                tracks.add(track)
            }
            if (tracks.isEmpty()) Log.d("tracks is empty", "why am i empty ${viewModel.observeAlbumTracks().value!!.items}")
            songRowAdapter.submitList(tracks)
            binding.songsRecyclerView.adapter = songRowAdapter
            binding.songsRecyclerView.layoutManager = LinearLayoutManager(activity)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("javaClass.simpleName", "destroyed")
        viewModel.observeAlbumTracks().removeObservers(this)
    }
}