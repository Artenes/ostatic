package io.github.artenes.ostatic

import android.app.Application
import io.github.artenes.ostatic.db.ApplicationRepository
import java.io.FileOutputStream

class OstaticApplication : Application() {

    companion object {
        const val CACHED_DATABASE_NAME = "khinsider.sqlite"
        const val APPLICATION_DATABASE_NAME = "ostatic.db"

        lateinit var REPOSITORY: ApplicationRepository
    }

    override fun onCreate() {
        super.onCreate()
        REPOSITORY = ApplicationRepository(this)
        copyCachedDatabaseToInternalStorage()
    }

    private fun applicationDatabaseExists(): Boolean {
        return getDatabasePath(APPLICATION_DATABASE_NAME).exists()
    }

    private fun copyCachedDatabaseToInternalStorage() {
        val cachedDatabaseStream = assets.open(CACHED_DATABASE_NAME)
        val applicationDatabaseStream = FileOutputStream(getDatabasePath(APPLICATION_DATABASE_NAME))
        cachedDatabaseStream.copyTo(applicationDatabaseStream)
    }

}