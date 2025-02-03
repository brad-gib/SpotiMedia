package edu.cs371m.spotimedia.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import edu.cs371m.spotimedia.R
import edu.cs371m.spotimedia.databinding.FragmentOtherBinding
import edu.cs371m.spotimedia.glide.Glide
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

// XXX Write most of this file
class OtherFragment: Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentOtherBinding? = null
    private val binding get() = _binding!!

    private val args: OtherFragmentArgs by navArgs()
    private fun initAdapters(binding: FragmentOtherBinding) {
        val songRowAdapter = SongRowAdapter(viewModel) {
            Log.d("album", "going to album page: ${it.name}")
            Log.d("album stats: ", "here: $it")
            viewModel.fetchAlbumTracks(it.id)
            findNavController().navigate(OtherFragmentDirections.actionOtherFragmentToAlbumFragment(it))
        }

        val artistRowAdapter = ArtistRowAdapter(viewModel) {
            Log.d("artist", "going to artist page: ${it.name}")
            findNavController().navigate(OtherFragmentDirections.actionOtherFragmentToArtistFragment(it))
        }

        viewModel.observeTopSongs().observe(viewLifecycleOwner) {
            songRowAdapter.submitList(it)
        }

        viewModel.observeTopArtists().observe(viewLifecycleOwner) {
            artistRowAdapter.submitList(it)
        }
        binding.topArtists.adapter = artistRowAdapter
        binding.topArtists.layoutManager = LinearLayoutManager(activity)
        binding.topTracks.adapter = songRowAdapter
        binding.topTracks.layoutManager = LinearLayoutManager(activity)
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOtherBinding.inflate(inflater, container, false)

        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated2")
        initAdapters(binding)
        viewModel.fetchUserProfile(args.accessToken)
        viewModel.observeUserProfile().observe(viewLifecycleOwner) {
            binding.profileName.text = "${it.displayName}'s Profile"
            if(it.images.isNotEmpty()) {
                Glide.glideFetch(it.images[it.images.size - 1].url, it.images[it.images.size - 1].url, binding.profilePic)
            } else {
                binding.profilePic.setImageResource(R.drawable.ic_cloud_download_black_24dp)
            }
            binding.followerCount.text = it.followers.total.toString()
        }
    }
}