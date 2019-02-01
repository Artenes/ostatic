package io.github.artenes.ostatic.db

import android.content.Context
import io.github.artenes.ostatic.api.JsoupHtmlDocumentReader
import io.github.artenes.ostatic.api.KhinsiderRepository

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

    fun searchAlbum(query: String): List<TopAlbumView> {
        val results = khRepo.searchAlbums(query)
        return results.map {
            TopAlbumView(it.id, it.name, "", "", "", 0, 0, it.cover)
        }
    }

}