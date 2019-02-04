package io.github.artenes.ostatic.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import io.github.artenes.ostatic.OstaticApplication

@Database(
    version = 1,
    entities = [AlbumEntity::class, CoverEntity::class, SongEntity::class, TopAlbumEntity::class, FavoriteEntity::class]
)
abstract class ApplicationDatabase : RoomDatabase() {

    companion object {

        private var INSTANCE: ApplicationDatabase? = null

        fun getInstance(context: Context): ApplicationDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context,
                    ApplicationDatabase::class.java,
                    OstaticApplication.APPLICATION_DATABASE_NAME
                ).build()
            }

            return INSTANCE as ApplicationDatabase
        }

    }

    abstract fun albumDao(): AlbumDao

}