package edu.cs371m.spotimedia.ui


import android.app.Application
import android.util.Log
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.navigation.NavController
import edu.cs371m.spotimedia.databinding.ActionBarBinding
import edu.cs371m.spotimedia.User
import edu.cs371m.spotimedia.ViewModelDBHelper
import edu.cs371m.spotimedia.api.AlbumTracks
import edu.cs371m.spotimedia.api.Artist
import edu.cs371m.spotimedia.api.SongRepo
import edu.cs371m.spotimedia.api.SpotAPI
import edu.cs371m.spotimedia.api.SpotProfile
import edu.cs371m.spotimedia.api.SpotifyTopResponse
import edu.cs371m.spotimedia.api.Track
import edu.cs371m.spotimedia.invalidUser
import edu.cs371m.spotimedia.model.TokenPair
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel(application: Application, private val state: SavedStateHandle) :
AndroidViewModel(application) {
    private var actionBarBinding : ActionBarBinding? = null
    private var api : SpotAPI = SpotAPI.create()
    var repo : SongRepo = SongRepo(api)
    private val dbHelp = ViewModelDBHelper()
    lateinit var navController: NavController
    fun initActionBarBinding(it: ActionBarBinding) {
        // XXX Write me, one liner
        actionBarBinding = it
    }
    // Track current authenticated user
    private var currentAuthUser = invalidUser

    var currAccessToken: String? = null

    private var userProfile = MutableLiveData<SpotProfile>()

    private var topSongs = MutableLiveData<List<Track>>()

    private var topArtists = MutableLiveData<List<Artist>>()
    private var currAlbumTracks = MutableLiveData<AlbumTracks>()

    private var profiles = MutableLiveData<ArrayList<SpotProfile>>()

    var userUID: String = ""

    fun observeProfiles(): LiveData<ArrayList<SpotProfile>> {
        return profiles
    }

    fun observeUserProfile(): LiveData<SpotProfile> {
        return userProfile
    }

    fun observeTopSongs(): LiveData<List<Track>> {
        return topSongs
    }

    fun observeTopArtists(): LiveData<List<Artist>> {
        return topArtists
    }
    fun observeAlbumTracks(): LiveData<AlbumTracks> {
        return currAlbumTracks
    }

    fun hideActionBarItems() {
        actionBarBinding?.actionTitle?.visibility = View.INVISIBLE
    }

    fun showActionBarItems() {
        actionBarBinding?.actionTitle?.visibility = View.VISIBLE
    }

    fun setCurrentAuthUser(user: User) {
        currentAuthUser = user
    }

    fun getAllProfiles() {
        profiles.postValue(ArrayList<SpotProfile>()) // make sure not null
        dbHelp.fetchTokens {
            for(tokenPair in it) {
                repo.getCurrentUserProfile(tokenPair.accessToken, object : Callback<SpotProfile> {
                    override fun onResponse(
                        call: Call<SpotProfile>,
                        response: Response<SpotProfile>
                    ) {
                        if(response.isSuccessful) {
                            response.body()?.let {
                                val list = profiles.value!!
                                it.userUID = tokenPair.userUID
                                list.add(it)
                                profiles.postValue(list)
                            }
                        }
                    }

                    override fun onFailure(call: Call<SpotProfile>, t: Throwable) {
                        // do nothing
                    }
                })
            }
        }
    }
    fun fetchUserProfile(accessToken: String) {  // Replace with your actual token
        accessToken?.let {repo.getCurrentUserProfile(it, object : Callback<SpotProfile> {
            override fun onFailure(call: Call<SpotProfile>, t: Throwable) {
                Log.e("----------------3", "Error fetching user profile", t)
            }
            override fun onResponse(call: Call<SpotProfile>, response: Response<SpotProfile>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d("----------------3", "User Profile: $it")
                        userProfile.postValue(it)
                    }
                } else {
                    Log.e("----------------3", "Failed to fetch user profile: ${response.errorBody()?.string()}")
                }
            }
        })}
    }
    fun fetchTopSongs() {  // Replace with your actual token
        currAccessToken?.let {repo.getTopTracks(it, object :
            Callback<SpotifyTopResponse<Track>> {
            override fun onFailure(call: Call<SpotifyTopResponse<Track>>, t: Throwable) { Log.d("----------------3", "Error fetching top songs", t) }
            override fun onResponse(call: Call<SpotifyTopResponse<Track>>,
                                    response: Response<SpotifyTopResponse<Track>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d("----------------3", "Top Songs: $it")
                        topSongs.postValue(it.items)
                    }
                } else Log.e("----------------3", "Failed to fetch top songs: ${response.errorBody()?.string()}")
            }
        })}
    }
    fun fetchTopArtists() {  // Replace with your actual token
        currAccessToken?.let {repo.getTopArtists(it, object :
            Callback<SpotifyTopResponse<Artist>> {
            override fun onFailure(call: Call<SpotifyTopResponse<Artist>>, t: Throwable) { Log.d("----------------3", "Error fetching top artists", t) }
            override fun onResponse(call: Call<SpotifyTopResponse<Artist>>,
                                    response: Response<SpotifyTopResponse<Artist>>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d("----------------3", "Top Artists: $it")
                        topArtists.postValue(it.items)
                    }
                } else Log.e("----------------3", "Failed to fetch top artists: ${response.errorBody()?.string()}")
            }
        })}
    }
    fun fetchAlbumTracks(id: String) {  // Replace with your actual token
        currAccessToken?.let {repo.getAlbumTracks(id, it, object :
            Callback<AlbumTracks> {
            override fun onFailure(call: Call<AlbumTracks>, t: Throwable) { Log.d("----------------3", "Error fetching album tracks", t) }
            override fun onResponse(call: Call<AlbumTracks>,
                                    response: Response<AlbumTracks>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        Log.d("----------------3", "Album Tracks: $it")
                        Log.d("album tracks: ", it.toString())
                        currAlbumTracks.postValue(it)
                    }
                } else Log.e("----------------3", "Failed to fetch album tracks: ${response.errorBody()?.string()}")
            }
        })}
    }
    fun addAccount(mAccessToken: String?) {
        if (state.contains(currentAuthUser.uid)) {
//            Log.d("adding user", "why am i here im already added ${currentAuthUser.uid}")
        } else {
//            Log.d("adding user", "added user: ${currentAuthUser.uid} with access token: $mAccessToken")
            state[currentAuthUser.uid] = mAccessToken
            dbHelp.createToken(TokenPair(userUID = currentAuthUser.uid, accessToken = mAccessToken!!))
            currAccessToken = mAccessToken
        }
    }
    fun userExists(userUID: String, resultListener: (Boolean) -> Unit) {
        dbHelp.fetchTokens {
            for (tokenPair in it) {
                Log.d("TOKEN PAIR", tokenPair.toString())
                if(tokenPair.userUID == userUID) {
                    state[currentAuthUser.uid] = tokenPair.accessToken
                    currAccessToken = tokenPair.accessToken
                    resultListener(true)
                    return@fetchTokens
                }
            }
            resultListener(false)
        }
    }
    fun statething(): SavedStateHandle {
        return state
    }
}