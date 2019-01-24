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

data class TopAlbumView(
    var id: String,
    var name: String,
    var size: String,
    var added: String,
    var time: String,
    var files: Int,
    var position: Int
)