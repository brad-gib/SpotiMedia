package edu.cs371m.spotimedia.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import edu.cs371m.spotimedia.R
import edu.cs371m.spotimedia.api.SpotProfile
import edu.cs371m.spotimedia.databinding.RowProfileBinding
import edu.cs371m.spotimedia.glide.Glide

class ProfileRowAdapter(private val viewModel: MainViewModel, callback: (String) -> Unit) :
    ListAdapter<SpotProfile, ProfileRowAdapter.VH>(ProfileDiff()) {
        private var callback = callback
    class ProfileDiff : DiffUtil.ItemCallback<SpotProfile>() {
        override fun areItemsTheSame(oldItem: SpotProfile, newItem: SpotProfile): Boolean {
            return areContentsTheSame(oldItem, newItem)
        }

        override fun areContentsTheSame(
            oldItem: SpotProfile,
            newItem: SpotProfile
        ): Boolean {
            return oldItem.followers == newItem.followers &&
                    oldItem.displayName == newItem.displayName
        }
    }

    inner class VH(val rowBinding: RowProfileBinding) : RecyclerView.ViewHolder(rowBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProfileRowAdapter.VH {
        val rowBinding = RowProfileBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false)
        return VH(rowBinding)
    }

    override fun onBindViewHolder(holder: ProfileRowAdapter.VH, position: Int) {
        val profile = getItem(position)
        holder.rowBinding.profileName.text = profile.displayName
        holder.rowBinding.followerCount.text = profile.followers.total.toString()
        if(profile.images.isNotEmpty()) Glide.glideFetch(profile.images[0].url, profile.images[0].url, holder.rowBinding.profilePic)
        else holder.rowBinding.profilePic.setBackgroundResource(R.drawable.ic_cloud_download_black_24dp)
        holder.rowBinding.root.setOnClickListener {
            val uid = profile.userUID
            viewModel.userExists(uid) {
                if(it) {
                    // nav to new fragment
                    callback(viewModel.currAccessToken!!)
                }
            }
        }
    }
}