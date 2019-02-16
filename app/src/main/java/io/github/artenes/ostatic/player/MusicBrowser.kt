package io.github.artenes.ostatic.player

import android.net.Uri
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
        const val BUNDLE_KEY_CATEGORY = "category"

    }

    fun getRoot(): MediaBrowserServiceCompat.BrowserRoot {
        return MediaBrowserServiceCompat.BrowserRoot(OSTATIC_ROOT, null)
    }

    suspend fun getChild(parentId: String): MutableList<MediaBrowserCompat.MediaItem> {
        return when (parentId) {
            OSTATIC_ROOT -> getRootMediaItems()
            OSTATIC_ALBUMS_LIST -> mutableListOf()
            OSTATIC_FAVORITE_SONGS -> mutableListOf()
            OSTATIC_ALBUM -> mutableListOf()
            OSTATIC_SONG -> mutableListOf()
            else -> mutableListOf()
        }
    }

    private suspend fun getRootMediaItems(): MutableList<MediaBrowserCompat.MediaItem> {
        return repo.getRecentAndTopAlbums().map {
            val albumUri = Uri.parse(OSTATIC_ALBUM).buildUpon().appendPath(it.id).build()
            val iconUri = if (it.cover.isEmpty()) null else Uri.parse(it.cover)

            val mediaDescription =
                MediaDescriptionCompat.Builder()
                    .setMediaId(albumUri.toString())
                    .setTitle(it.name)
                    .setIconUri(iconUri)
                    .setMediaUri(albumUri)
                    .setExtras(bundleOf(BUNDLE_KEY_CATEGORY to it.category))
                    .build()

            MediaBrowserCompat.MediaItem(mediaDescription, MediaBrowserCompat.MediaItem.FLAG_BROWSABLE)
        }.toMutableList()
    }

}