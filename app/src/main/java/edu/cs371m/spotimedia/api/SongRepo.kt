package edu.cs371m.spotimedia.api

import retrofit2.Callback

class SongRepo(private val spotApi: SpotAPI) {
    fun getCurrentUserProfile(accessToken: String, callback: Callback<SpotProfile>) {
        val call = spotApi.getCurrentUserProfile("Bearer $accessToken")
        call.enqueue(callback)
    }
    fun getTopArtists(accessToken: String, callback: Callback<SpotifyTopResponse<Artist>>) {
        val call = spotApi.getTopArtists("Bearer $accessToken")
        call.enqueue(callback)
    }
    fun getTopTracks(accessToken: String, callback: Callback<SpotifyTopResponse<Track>>) {
        val call = spotApi.getTopTracks("Bearer $accessToken")
        call.enqueue(callback)
    }
    fun getAlbumTracks(id: String, accessToken: String, callback: Callback<AlbumTracks>) {
        val call = spotApi.getAlbumTracks(id, "Bearer $accessToken")
        call.enqueue(callback)
    }
}
