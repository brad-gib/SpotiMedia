package edu.cs371m.spotimedia.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import edu.cs371m.spotimedia.R
import edu.cs371m.spotimedia.api.Track
import edu.cs371m.spotimedia.databinding.FragmentArtistBinding
import edu.cs371m.spotimedia.glide.Glide

class ArtistFragment: Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentArtistBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private val args: ArtistFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentArtistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")
        binding.artistName.text = args.post.name
        if(args.post.images.isNotEmpty()) Glide.glideFetch(args.post.images[0].url, args.post.images[0].url, binding.artistPic)
        else binding.artistPic.setBackgroundResource(R.drawable.ic_cloud_download_black_24dp)
        binding.followers.text = "Followers: ${args.post.followers.total}"
        val songRowAdapter = SongRowAdapter(viewModel) {}
        val allSongs = viewModel.observeTopSongs().value
        val songs : ArrayList<Track> = ArrayList()
        for (song in allSongs!!) {
            for (artist in song.artists) {
                if (artist.name == args.post.name) {
                    songs.add(song)
                    break
                }
            }
        }
        if (songs.isEmpty()) binding.emptyTV.text = "You have no top songs from this artist!"

        songRowAdapter.submitList(songs)
        binding.recyclerView.adapter = songRowAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(activity)
    }
}