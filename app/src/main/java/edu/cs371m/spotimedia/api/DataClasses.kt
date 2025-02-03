package edu.cs371m.spotimedia.api

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class SpotProfile (
    @SerializedName("display_name")
    val displayName: String,
    @SerializedName("followers")
    val followers: Followers,
    @SerializedName("images")
    val images: List<Image>,
    var userUID: String
): Serializable
data class Followers(
    @SerializedName("total")
    val total: Int
): Serializable
data class Image(
    @SerializedName("url")
    val url: String,
    @SerializedName("height")
    val height: Int?,
    @SerializedName("width")
    val width: Int?
): Serializable
data class SpotifyTopResponse<T>(
    @SerializedName("items")
    val items: List<T>,
    @SerializedName("total")
    val total: Int,
    @SerializedName("limit")
    val limit: Int,
    @SerializedName("offset")
    val offset: Int,
    @SerializedName("previous")
    val previous: String?,
    @SerializedName("next")
    val next: String?
): Serializable
data class Artist(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("popularity")
    val popularity: Int,
    @SerializedName("genres")
    val genres: List<String>,
    @SerializedName("images")
    val images: List<Image>,
    @SerializedName("followers")
    val followers: Followers
): Serializable
data class Track(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("album")
    val album: Album,
    @SerializedName("artists")
    val artists: List<Artist>,
    @SerializedName("popularity")
    val popularity: Int
): Serializable
data class Album(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("release_date") val release_date: String,
    @SerializedName("images") val images: List<Image>,
    @SerializedName("artists") val artists: List<SimpleArtist>
): Serializable
data class AlbumTracks(
    @SerializedName("items") val items: List<SimpleTrack>,
    @SerializedName("total") val total: Int
): Serializable
data class SimpleTrack(
    @SerializedName("name") val name: String,
    @SerializedName("track_number") val trackNumber: Int,
): Serializable
data class SimpleArtist(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    ): Serializable