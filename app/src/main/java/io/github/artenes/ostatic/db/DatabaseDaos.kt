package io.github.artenes.ostatic.db

import androidx.room.Dao
import androidx.room.Query

@Dao
interface AlbumDao {

    @Query("select position, id, name, files, size, added, time from top_40 inner join albums on id = album_id order by position ASC")
    suspend fun getTop40(): List<TopAlbumView>

}