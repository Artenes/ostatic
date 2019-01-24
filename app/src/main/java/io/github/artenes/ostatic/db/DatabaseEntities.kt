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

data class TopAlbumView(
    var id: String,
    var name: String,
    var size: String,
    var added: String,
    var time: String,
    var files: Int,
    var position: Int
)