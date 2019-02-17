package io.github.artenes.ostatic.player

import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import androidx.core.os.bundleOf
import androidx.media.MediaBrowserServiceCompat
import io.github.artenes.ostatic.db.ApplicationRepository

class MusicBrowser(private val repo: ApplicationRepository) {

    companion object {

        const val OSTATIC_ROOT = "https://ostatic.io/"
        const val OSTATIC_ALBUMS_LIST = "https://ostatic.io/albums/"
        const val OSTATIC_FAVORITE_SONGS = "https://ostatic.io/favorites/"
        const val OSTATIC_ALBUM = "https://ostatic.io/album/"
        const val OSTATIC_SONG = "https://ostatic.io/song/"

        const val BUNDLE_KEY_LIMIT = "limit"
        const val BUNDLE_KEY_CATEGORY = "category"

        const val TOP_ALBUM_DEFAULT_LIMIT = 7

    }

    fun getRoot(): MediaBrowserServiceCompat.BrowserRoot {
        return MediaBrowserServiceCompat.BrowserRoot(OSTATIC_ROOT, null)
    }

    suspend fun getChild(parentId: String, bundle: Bundle): MutableList<MediaBrowserCompat.MediaItem> {
        return when (parentId) {
            OSTATIC_ROOT -> getRootMediaItems(bundle)
            OSTATIC_ALBUMS_LIST -> mutableListOf()
            OSTATIC_FAVORITE_SONGS -> mutableListOf()
            OSTATIC_ALBUM -> mutableListOf()
            OSTATIC_SONG -> mutableListOf()
            else -> mutableListOf()
        }
    }

    private suspend fun getRootMediaItems(bundle: Bundle): MutableList<MediaBrowserCompat.MediaItem> {
        val limit =
            if (bundle.containsKey(BUNDLE_KEY_LIMIT)) bundle.getInt(BUNDLE_KEY_LIMIT) else TOP_ALBUM_DEFAULT_LIMIT
        return repo.getRecentAndTopAlbums(limit).map {
            val albumUri = Uri.parse(OSTATIC_ALBUM).buildUpon().appendPath(it.id).build()
            val iconUri = Uri.parse(it.cover)

            val mediaDescription =
                MediaDescriptionCompat.Builder()
                    .setMediaId(it.id)
                    .setTitle(it.name)
                    .setIconUri(iconUri)
                    .setMediaUri(albumUri)
                    .setExtras(bundleOf(BUNDLE_KEY_CATEGORY to it.category))
                    .build()

            MediaBrowserCompat.MediaItem(mediaDescription, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE)
        }.toMutableList()
    }

}