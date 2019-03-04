package io.github.artenes.ostatic.model

/**
 * The possible categories of an album
 */
object AlbumCategory {

    const val CATEGORY_RECENT = "recent"
    const val CATEGORY_TOP_40 = "top_40"
    const val CATEGORY_TOP_6_MONTHS = "top_6_months"
    const val CATEGORY_TOP_NEWLY = "top_newly_added"
    const val CATEGORY_TOP_ALL = "top_all_time"
    const val CATEGORY_FAVORITES = "favorites"

    fun isCategory(value: String): Boolean {
        return when(value) {
            AlbumCategory.CATEGORY_RECENT -> true
            AlbumCategory.CATEGORY_TOP_40 -> true
            AlbumCategory.CATEGORY_TOP_6_MONTHS -> true
            AlbumCategory.CATEGORY_TOP_NEWLY -> true
            AlbumCategory.CATEGORY_TOP_ALL -> true
            AlbumCategory.CATEGORY_FAVORITES -> true
            else -> false
        }
    }

}

/**
 * The available uris for the various resources of the application
 */
object Ostatic {

    const val ROOT = "https://ostatic.io"
    const val ALBUMS_LIST = "$ROOT/albums"
    const val FAVORITE_SONGS = "$ROOT/favorites"
    const val ALBUM = "$ROOT/album"
    const val SONG = "$ROOT/song"

    const val RECENT = "$ALBUMS_LIST/${AlbumCategory.CATEGORY_RECENT}"
    const val FAVORITES = "$ALBUMS_LIST/${AlbumCategory.CATEGORY_FAVORITES}"
    const val TOP_40 = "$ALBUMS_LIST/${AlbumCategory.CATEGORY_TOP_40}"
    const val TOP_6_MONTHS = "$ALBUMS_LIST/${AlbumCategory.CATEGORY_TOP_6_MONTHS}"
    const val TOP_NEWLY = "$ALBUMS_LIST/${AlbumCategory.CATEGORY_TOP_NEWLY}"
    const val TOP_ALL = "$ALBUMS_LIST/${AlbumCategory.CATEGORY_TOP_ALL}"

    fun makeAlbumPath(id: String): String {
        return "$ALBUMS_LIST/$id"
    }

    fun getIdFromUri(uri: String): String {
        return uri.split("/").last()
    }

}

/**
 * An album with its category
 */
open class AlbumWithCategory(val id: String, val name: String, val cover: String, val category: String)

class AllAlbumsCategory(category: String) : AlbumWithCategory(category, category, "", category)

/**
 * Album for showcase, usually used in top listings
 */
data class AlbumForShowcase(val id: String, val name: String, val cover: String, val uri: String) {

    fun isCategory(): Boolean {
        return when(id) {
            AlbumCategory.CATEGORY_RECENT -> true
            AlbumCategory.CATEGORY_TOP_40 -> true
            AlbumCategory.CATEGORY_TOP_6_MONTHS -> true
            AlbumCategory.CATEGORY_TOP_ALL -> true
            AlbumCategory.CATEGORY_TOP_NEWLY -> true
            else -> false
        }
    }

}