package io.github.artenes.ostatic.view

import android.support.v4.media.MediaBrowserCompat

/**
 * Some constants used for some view logic across the application
 */
object ViewConstants {

    //the number of albums to show in a top listing
    const val TOP_ALBUM_DEFAULT_LIMIT = 7
    const val ALL_ALBUMS_COVER = "android.resource://io.github.artenes.ostatic/drawable/album"
}

/**
 * A section of a listing that contains a list of albums
 */
data class AlbumSection(
    val title: String,
    val subtitle: String,
    val albums: List<MediaBrowserCompat.MediaItem>,
    val ofCategory: String,
    val isHighlight: Boolean = false
)