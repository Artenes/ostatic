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

    @Query("select id, name, files, size, added, time, (select url from covers where covers.album_id = albums.id limit 1) as cover from albums where id = :id")
    suspend fun getAlbum(id: String): AlbumView

    @Query("select songs.id, songs.name, songs.track, songs.time, songs.url, songs.album_id, albums.name as album_name, (select url from covers where covers.album_id = :id limit 1) as album_cover from songs inner join albums on albums.id = songs.album_id where album_id = :id order by track asc")
    suspend fun getSongs(id: String): List<SongView>

}