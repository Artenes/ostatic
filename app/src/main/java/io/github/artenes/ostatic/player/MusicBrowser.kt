package io.github.artenes.ostatic.player

import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import androidx.core.os.bundleOf
import androidx.media.MediaBrowserServiceCompat
import io.github.artenes.ostatic.db.ApplicationRepository
import io.github.artenes.ostatic.model.Ostatic

class MusicBrowser(private val repo: ApplicationRepository) {

    companion object {
        private const val DEFAULT_ALBUMS_LIMIT = 10
        const val BUNDLE_KEY_LIMIT = "limit"
        const val BUNDLE_KEY_CATEGORY = "category"
    }

    fun getRoot(): MediaBrowserServiceCompat.BrowserRoot {
        return MediaBrowserServiceCompat.BrowserRoot(Ostatic.ROOT, null)
    }

    suspend fun getChild(parentId: String, bundle: Bundle): MutableList<MediaBrowserCompat.MediaItem> {
        return when (parentId) {
            Ostatic.ROOT -> getRootMediaItems(bundle)
            Ostatic.ALBUMS_LIST -> mutableListOf()
            Ostatic.FAVORITE_SONGS -> mutableListOf()
            Ostatic.ALBUM -> mutableListOf()
            Ostatic.SONG -> mutableListOf()
            else -> mutableListOf()
        }
    }

    private suspend fun getRootMediaItems(bundle: Bundle): MutableList<MediaBrowserCompat.MediaItem> {
        val limit =
            if (bundle.containsKey(BUNDLE_KEY_LIMIT)) bundle.getInt(BUNDLE_KEY_LIMIT) else DEFAULT_ALBUMS_LIMIT
        return repo.getRecentAndTopAlbums(limit).map {
            val albumUri = Uri.parse(Ostatic.makeAlbumPath(it.id))
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