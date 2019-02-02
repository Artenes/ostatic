package io.github.artenes.ostatic.db

import android.content.Context
import io.github.artenes.ostatic.api.JsoupHtmlDocumentReader
import io.github.artenes.ostatic.api.KhinsiderRepository
import io.github.artenes.ostatic.model.Album

class ApplicationRepository(context: Context) {

    private val db: ApplicationDatabase = ApplicationDatabase.getInstance(context)
    private val khRepo: KhinsiderRepository = KhinsiderRepository(JsoupHtmlDocumentReader())

    suspend fun getTop40(limit: Int = 40): List<TopAlbumView> {
        return db.albumDao().getTop40(limit)
    }

    suspend fun getTop100AllTime(limit: Int = 100): List<TopAlbumView> {
        return db.albumDao().getTop100AllTime(limit)
    }

    suspend fun getTop100Last6Months(limit: Int = 100): List<TopAlbumView> {
        return db.albumDao().getTop100Last6Months(limit)
    }

    suspend fun getTop100NewlyAdded(limit: Int = 100): List<TopAlbumView> {
        return db.albumDao().getTop100NewlyAdded(limit)
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

    fun searchAlbum(query: String): List<TopAlbumView> {
        val results = khRepo.searchAlbums(query)
        return results.map {
            TopAlbumView(it.id, it.name, "", "", "", 0, 0, it.cover)
        }
    }

}