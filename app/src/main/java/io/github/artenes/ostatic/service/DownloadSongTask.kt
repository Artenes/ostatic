package io.github.artenes.ostatic.service

import android.app.DownloadManager
import android.content.res.Resources
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import io.github.artenes.ostatic.OstaticApplication
import io.github.artenes.ostatic.R
import io.github.artenes.ostatic.db.SongView

class DownloadSongTask(private val manager: DownloadManager, private val resources: Resources) : AsyncTask<SongView, Void, Unit>() {

    private val repo = OstaticApplication.REPOSITORY

    override fun doInBackground(vararg params: SongView) {

        for (song in params) {
            val url = repo.getSongMp3Url(song.id)
            if (url.isEmpty()) {
                continue
            }
            val uri = Uri.parse(url)
            val request = DownloadManager.Request(uri)
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
            request.setAllowedOverRoaming(false)
            request.setTitle(resources.getString(R.string.downloading_song, song.name))
            request.setDescription(resources.getString(R.string.downloading_in_progress))
            request.setVisibleInDownloadsUi(true)
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "Ostatic/${song.name}")
            manager.enqueue(request)
        }

    }

}