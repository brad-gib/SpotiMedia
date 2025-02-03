package edu.cs371m.spotimedia.api

import android.text.SpannableString
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import java.lang.reflect.Type
interface SpotAPI {
    @GET("v1/me")
    fun getCurrentUserProfile(@Header("Authorization") authHeader: String): Call<SpotProfile>
    @GET("v1/me/top/artists")
    fun getTopArtists(@Header("Authorization") authHeader: String): Call<SpotifyTopResponse<Artist>>

    @GET("v1/me/top/tracks")
    fun getTopTracks(@Header("Authorization") authHeader: String): Call<SpotifyTopResponse<Track>>

    @GET("v1/albums/{albumId}/tracks")
    fun getAlbumTracks(
        @Path("albumId") albumId: String,
        @Header("Authorization") authorization: String
    ): Call<AlbumTracks>

    class SpannableDeserializer : JsonDeserializer<SpannableString> {
        // @Throws(JsonParseException::class)
        override fun deserialize(
            json: JsonElement,
            typeOfT: Type,
            context: JsonDeserializationContext
        ): SpannableString {
            return SpannableString(json.asString)
        }
    }

    companion object {
        // Tell Gson to use our SpannableString deserializer
        private fun buildGsonConverterFactory(): GsonConverterFactory {
            val gsonBuilder = GsonBuilder().registerTypeAdapter(
                SpannableString::class.java, SpannableDeserializer()
            )
            return GsonConverterFactory.create(gsonBuilder.create())
        }
        // Keep the base URL simple
        private var httpurl = HttpUrl.Builder()
            .scheme("https")
            .host("api.spotify.com")
            .build()
        fun create(): SpotAPI = create(httpurl)
        private fun create(httpUrl: HttpUrl): SpotAPI {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    // Enable basic HTTP logging to help with debugging.
                    this.level = HttpLoggingInterceptor.Level.BASIC
                })
                .build()
            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
                .addConverterFactory(buildGsonConverterFactory())
                .build()
                .create(SpotAPI::class.java)
        }
    }
}