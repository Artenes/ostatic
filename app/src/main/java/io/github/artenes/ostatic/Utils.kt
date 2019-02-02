package io.github.artenes.ostatic

import android.widget.ImageView
import com.squareup.picasso.Picasso

fun Picasso.loadAlbumCover(cover: String?, imageView: ImageView, size: Int = 280) {

    val albumUrl = if (cover.isNullOrEmpty()) "android.resource://io.github.artenes.ostatic/drawable/album" else cover

    Picasso.get()
        .load(albumUrl)
        .resize(size, size)
        .centerCrop()
        .placeholder(R.drawable.album)
        .error(R.drawable.album)
        .into(imageView)

    imageView.tag = true

}