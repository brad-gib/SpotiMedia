package edu.cs371m.spotimedia.glide

import android.content.Context
import android.content.res.Resources
import android.text.Html
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.GlideBuilder
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.module.AppGlideModule
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.StorageReference
import edu.cs371m.spotimedia.R

@GlideModule
class AppGlideModule : AppGlideModule() {
    override fun applyOptions(context: Context, builder: GlideBuilder) {
        // You can change this to make Glide more verbose
        builder.setLogLevel(Log.ERROR)
    }
    override fun isManifestParsingEnabled(): Boolean {
        return false
    }
}
object Glide {
    private val width = Resources.getSystem().displayMetrics.widthPixels
    private val height = Resources.getSystem().displayMetrics.heightPixels
    private var glideOptions = RequestOptions ()
        .fitCenter()
        .transform(RoundedCorners (20))
    fun fetch(storageReference: StorageReference, imageView: ImageView) {
        val width = 400
        val height = 400
        GlideApp.with(imageView.context)
            .asBitmap()
            .load(storageReference)
            .apply(glideOptions)
            .error(android.R.color.holo_red_dark)
            .override(width, height)
            .into(imageView)
    }
    private fun fromHtml(source: String): String {
        return Html.fromHtml(source, Html.FROM_HTML_MODE_LEGACY).toString()
    }
    fun glideFetch(urlString: String, thumbnailURL: String, imageView: ImageView) {
        GlideApp.with(imageView.context)
            .asBitmap() // Try to display animated Gifs and video still
            .load(fromHtml(urlString))
            .apply(glideOptions)
            .error(R.color.colorAccent)
            .override(width, height)
            .error(
                GlideApp.with(imageView.context)
                    .asBitmap()
                    .load(fromHtml(thumbnailURL))
                    .apply(glideOptions)
                    .error(R.color.colorAccent)
                    .override(500, 500)
            )
            .into(imageView)
    }
}
