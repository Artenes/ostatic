package io.github.artenes.ostatic.db

import androidx.room.*

@Dao
interface AlbumDao {

    @Query("select id, name, files, size, added, time, (select url from covers where covers.album_id = albums.id limit 1) as cover from albums where id = :id")
    suspend fun getAlbum(id: String): AlbumView

    @Query("select songs.id, songs.name, songs.track, songs.time, songs.url, songs.album_id, albums.name as album_name, (select url from covers where covers.album_id = :id limit 1) as album_cover from songs inner join albums on albums.id = songs.album_id where album_id = :id order by track asc")
    suspend fun getSongs(id: String): List<SongView>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(album: AlbumEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSong(song: SongEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCover(cover: CoverEntity)

    @Query("select * from top_albums where type = :type limit :limit")
    suspend fun getTopAlbums(type: String, limit: Int = 100): List<TopAlbumEntity>

    @Query("select * from top_albums where id = :id and type = :type")
    suspend fun getTopAlbum(id: String, type: String): TopAlbumEntity?

    @Query("select * from top_albums where id = :id")
    suspend fun getTopAlbum(id: String): List<TopAlbumEntity>

    @Update
    suspend fun updateTopAlbum(topAlbum: TopAlbumEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopAlbum(topAlbum: TopAlbumEntity)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun markAsRecent(album: TopAlbumEntity)

    @Query("select * from top_albums where type = 'top_recent' order by updated_at desc limit :limit")
    suspend fun getRecentAlbums(limit: Int): List<TopAlbumEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Delete
    suspend fun removeFavorite(favorite: FavoriteEntity)

    @Query("select 0 as position, id, name, files, size, added, time, (select url from covers where covers.album_id = albums.id limit 1) as cover from albums inner join favorites on favorites.entity_id = albums.id")
    suspend fun getFavoriteAlbums(): List<TopAlbumView>

    @Query("select songs.id, songs.name, songs.track, songs.time, songs.url, songs.album_id, albums.name as album_name, (select url from covers where covers.album_id = songs.album_id limit 1) as album_cover from songs inner join albums on albums.id = songs.album_id inner join favorites on favorites.entity_id = songs.id")
    suspend fun getFavoriteSongs(): List<SongView>

    @Query("select * from favorites where entity_id = :entityId")
    suspend fun getFavorite(entityId: String): FavoriteEntity?

    @Query("update songs set url = :url where id = :id")
    fun updateSongMp3UrlNonSuspend(id: String, url: String)

    @Query("select url from songs where id = :songId")
    fun getSongMp3UrlNonSuspend(songId: String): String

}