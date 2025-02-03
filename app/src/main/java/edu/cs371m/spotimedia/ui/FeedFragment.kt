package edu.cs371m.spotimedia.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import edu.cs371m.spotimedia.databinding.FragmentFeedBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FeedFragment : Fragment() {
    private val viewModel: MainViewModel by activityViewModels()
    private var _binding: FragmentFeedBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(javaClass.simpleName, "onViewCreated")
        viewModel.hideActionBarItems()
        //get list of profiles from database and submit it to profile adapter
        viewModel.getAllProfiles()
        viewModel.observeProfiles().observe(viewLifecycleOwner) {
            val profileAdapter = ProfileRowAdapter(viewModel) {token ->
                findNavController().navigate(FeedFragmentDirections.actionFeedFragmentToOtherFragment(token))
                GlobalScope.launch(Dispatchers.IO) {
                    viewModel.fetchTopArtists()
                    viewModel.fetchTopSongs()
                    viewModel.fetchUserProfile(viewModel.currAccessToken!!)
                }
            }
            profileAdapter.submitList(it)
            binding.recyclerView.adapter = profileAdapter
            binding.recyclerView.layoutManager = LinearLayoutManager(activity)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.showActionBarItems()
    }
}