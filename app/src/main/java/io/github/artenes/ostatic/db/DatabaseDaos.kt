package io.github.artenes.ostatic.db

import androidx.room.Dao
import androidx.room.Query

@Dao
interface AlbumDao {

    @Query("select position, id, name, files, size, added, time, (select url from covers where covers.album_id = albums.id limit 1) as cover from top_40 inner join albums on id = album_id order by position ASC limit :limit")
    suspend fun getTop40(limit: Int = 40): List<TopAlbumView>

    @Query("select position, id, name, files, size, added, time, (select url from covers where covers.album_id = albums.id limit 1) as cover from top_100_all_time inner join albums on id = album_id order by position ASC limit :limit")
    suspend fun getTop100AllTime(limit: Int = 100): List<TopAlbumView>

    @Query("select position, id, name, files, size, added, time, (select url from covers where covers.album_id = albums.id limit 1) as cover from top_100_last_6_months inner join albums on id = album_id order by position ASC limit :limit")
    suspend fun getTop100Last6Months(limit: Int = 100): List<TopAlbumView>

    @Query("select position, id, name, files, size, added, time, (select url from covers where covers.album_id = albums.id limit 1) as cover from top_100_newly_added inner join albums on id = album_id order by position ASC limit :limit")
    suspend fun getTop100NewlyAdded(limit: Int = 100): List<TopAlbumView>

}