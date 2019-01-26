package io.github.artenes.ostatic.db

import android.content.Context

class ApplicationRepository(context: Context) {

    private val db: ApplicationDatabase = ApplicationDatabase.getInstance(context)

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
        return db.albumDao().getTop100NewlyAdded(100)
    }

    suspend fun getAlbum(id: String): AlbumView {
        return db.albumDao().getAlbum(id)
    }

    suspend fun getSongs(id: String): List<SongView> {
        return db.albumDao().getSongs(id)
    }

}