package io.github.artenes.ostatic

import android.app.Application
import io.github.artenes.ostatic.db.ApplicationRepository
import io.github.artenes.ostatic.db.PreferencesRepository

class OstaticApplication : Application() {

    companion object {
        const val APPLICATION_DATABASE_NAME = "ostatic.db"

        lateinit var REPOSITORY: ApplicationRepository
        lateinit var PREFERENCES: PreferencesRepository
    }

    override fun onCreate() {
        super.onCreate()
        REPOSITORY = ApplicationRepository(this)
        PREFERENCES = PreferencesRepository(this)
    }

    //ROOM is now being used to manage the database
    //keep code just as reference
//    private fun applicationDatabaseExists(): Boolean {
//        return getDatabasePath(APPLICATION_DATABASE_NAME).exists()
//    }
//
//    private fun copyCachedDatabaseToInternalStorage() {
//        val cachedDatabaseStream = assets.open(CACHED_DATABASE_NAME)
//        val applicationDatabaseStream = FileOutputStream(getDatabasePath(APPLICATION_DATABASE_NAME))
//        cachedDatabaseStream.copyTo(applicationDatabaseStream)
//    }

}