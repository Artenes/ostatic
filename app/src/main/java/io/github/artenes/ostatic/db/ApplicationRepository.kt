package io.github.artenes.ostatic.db

import android.content.Context
import android.util.Log
import io.github.artenes.ostatic.api.JsoupHtmlDocumentReader
import io.github.artenes.ostatic.api.KhinsiderRepository
import io.github.artenes.ostatic.model.Album
import io.github.artenes.ostatic.model.TopAlbums
import java.util.*

class ApplicationRepository(context: Context) {

    companion object {

        const val TAG = "ApplicationRepository"

    }

    private val db: ApplicationDatabase = ApplicationDatabase.getInstance(context)
    private val khRepo: KhinsiderRepository = KhinsiderRepository(JsoupHtmlDocumentReader())
    private val mp3LinksCache: MutableMap<String, String> = mutableMapOf()

    suspend fun getTop40(limit: Int = 40): List<TopAlbumView> {
        return getTopAlbum("top_40", TopAlbums.TOP_40, limit)
    }

    suspend fun getTop100AllTime(limit: Int = 100): List<TopAlbumView> {
        return getTopAlbum("top_all_time", TopAlbums.ALL_TIME, limit)
    }

    suspend fun getTop100Last6Months(limit: Int = 100): List<TopAlbumView> {
        return getTopAlbum("top_6_months", TopAlbums.LAST_SIX_MOTHS, limit)
    }

    suspend fun getTop100NewlyAdded(limit: Int = 100): List<TopAlbumView> {
        return getTopAlbum("top_newly_added", TopAlbums.NEWLY_ADDED, limit)
    }

    suspend fun getAlbum(id: String): AlbumView {
        return db.albumDao().getAlbum(id)
    }

    suspend fun getSongs(id: String): List<SongView> {
        return db.albumDao().getSongs(id)
    }

    suspend fun getAlbumAndSongs(id: String): Pair<AlbumView, List<SongView>> {

        val localAlbum: AlbumView? = getAlbum(id)

        if (localAlbum == null) {

            val remoteAlbum: Album = khRepo.getAlbum(id)
            val albumEntity = AlbumEntity(
                remoteAlbum.id,
                remoteAlbum.name,
                remoteAlbum.totalFilesize,
                remoteAlbum.dateAdded,
                remoteAlbum.totalTime,
                remoteAlbum.numberOfFiles.toInt()
            )
            db.albumDao().insertAlbum(albumEntity)

            for (cover in remoteAlbum.images) {
                db.albumDao().insertCover(CoverEntity(0, remoteAlbum.id, cover))
            }

            for (song in remoteAlbum.songs) {
                db.albumDao().insertSong(SongEntity(song.id, song.name, song.track, song.time, "", song.albumId))
            }

            return AlbumView(
                albumEntity.id,
                albumEntity.name,
                albumEntity.size,
                albumEntity.added,
                albumEntity.time,
                albumEntity.files,
                remoteAlbum.cover
            ) to remoteAlbum.songs.map {
                SongView(it.id, it.name, it.track, it.time, "", it.albumId, remoteAlbum.name, remoteAlbum.cover)
            }

        }

        val localSong = getSongs(id)

        return localAlbum to localSong

    }

    suspend fun markAsRecent(album: AlbumView) {
        val recentAlbum = db.albumDao().getTopAlbum(album.id, "top_recent")
        if (recentAlbum == null) {
            db.albumDao().markAsRecent(TopAlbumEntity(album.id, album.name,album.cover ?: "","top_recent"))
        } else {
            recentAlbum.updatedAt = Calendar.getInstance().timeInMillis
            db.albumDao().updateTopAlbum(recentAlbum)
        }
    }

    suspend fun getRecentAlbums(limit: Int = 100): List<TopAlbumView> {
        return db.albumDao().getRecentAlbums(limit).map {
            TopAlbumView(it.id, it.name, "", "", "", 0, 0, it.cover)
        }
    }

    suspend fun getFavoriteAlbums(): List<TopAlbumView> {
        //reversed to show the most recent first
        return db.albumDao().getFavoriteAlbums().reversed()
    }

    suspend fun getFavoriteSongs(): List<SongView> {
        //reversed to show the most recent first
        return db.albumDao().getFavoriteSongs().reversed()
    }

    suspend fun getFavorite(id: String): FavoriteEntity? {
        return db.albumDao().getFavorite(id)
    }

    suspend fun toggleFavorite(id: String, type: String) {
        val favorite = db.albumDao().getFavorite(id)
        if (favorite == null) {
            db.albumDao().insertFavorite(FavoriteEntity(id, type))
        } else {
            db.albumDao().removeFavorite(favorite)
        }
    }

    fun searchAlbum(query: String): List<TopAlbumView> {
        val results = khRepo.searchAlbums(query) ?: return emptyList()
        return results.map {
            TopAlbumView(it.id, it.name, "", "", "", 0, 0, it.cover)
        }
    }

    fun getSongMp3Url(songId: String): String {
        var url: String? = mp3LinksCache[songId]
        if (url == null) {
            Log.d(TAG, "Fetching song $songId from KHInsider")
            url = khRepo.getMp3LinkForSong(songId)
            db.albumDao().updateSongMp3UrlNonSuspend(songId, url)
            Log.d(TAG, "Updated song $songId on database")
            mp3LinksCache[songId] = url
        } else {
            Log.d(TAG, "Fetching song $songId from cache")
        }
        return url as String
    }

    private suspend fun getTopAlbum(localType: String, remoteType: TopAlbums, limit: Int): List<TopAlbumView> {
        val albums: List<TopAlbumEntity> = db.albumDao().getTopAlbums(localType, limit)

        if (!albums.isEmpty()) {
            return albums.map { TopAlbumView(it.id, it.name, "", "", "", 0, 0, it.cover) }
        }

        val remoteAlbumsList = khRepo.getTopAlbums(remoteType)

        for (remoteAlbum in remoteAlbumsList) {
            val cover = if (remoteAlbum.cover.isNullOrEmpty()) "" else remoteAlbum.cover
            db.albumDao().insertTopAlbum(TopAlbumEntity(remoteAlbum.id, remoteAlbum.name, cover, localType))
        }

        return remoteAlbumsList.take(limit).map {
            TopAlbumView(it.id, it.name, "", "", "", 0, 0, it.cover)
        }
    }

}