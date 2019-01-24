package io.github.artenes.ostatic.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "albums")
data class AlbumEntity(
    @PrimaryKey var id: String,
    var name: String,
    var size: String,
    var added: String,
    var time: String,
    var files: Int
)

@Entity(tableName = "top_40")
data class Top40Entity(
    @PrimaryKey var album_id: String,
    var position: Int
)

@Entity(tableName = "top_100_all_time")
data class Top100AllTime(
    @PrimaryKey var album_id: String,
    var position: Int
)

@Entity(tableName = "top_100_last_6_months")
data class Top100Last6Months(
    @PrimaryKey var album_id: String,
    var position: Int
)

@Entity(tableName = "top_100_newly_added")
data class Top100NewlyAdded(
    @PrimaryKey var album_id: String,
    var position: Int
)

@Entity(tableName = "covers")
data class Covers(
    @PrimaryKey var id: Int,
    var album_id: String,
    var url: String
)

data class TopAlbumView(
    var id: String,
    var name: String,
    var size: String,
    var added: String,
    var time: String,
    var files: Int,
    var position: Int,
    var cover: String?
) {

    companion object {

        const val NEXT_PAGE_ID = "NEXT_PAGE_ID"

        fun makeNextPageAlbum(id: String = "", name: String = "", title: String = ""): TopAlbumView {
            return TopAlbumView(id, name, NEXT_PAGE_ID, title, "", 0, 0, "")
        }

    }

}