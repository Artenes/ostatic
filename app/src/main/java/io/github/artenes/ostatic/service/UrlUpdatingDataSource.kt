package io.github.artenes.ostatic.service

import android.net.Uri
import android.util.Log
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DataSpec
import com.google.android.exoplayer2.upstream.TransferListener
import io.github.artenes.ostatic.db.ApplicationRepository
import java.io.IOException

class UrlUpdatingDataSource private constructor(
    private val realDataSource: DataSource,
    private val repo: ApplicationRepository
) : DataSource {

    private var updatedDataSpec: DataSpec? = null

    class Factory(private val realFactory: DataSource.Factory, private val repo: ApplicationRepository) :
        DataSource.Factory {
        override fun createDataSource(): DataSource {
            return UrlUpdatingDataSource(realFactory.createDataSource(), repo)
        }
    }

    companion object {

        const val NO_SONG_URI = "http://nosong.ostatic/"

        const val TAG = "UrlUpdatingDataSource"

        fun makeNoSongUri(songId: String): Uri {
            return Uri.parse(NO_SONG_URI).buildUpon().appendPath(songId).build()
        }

        fun isNoSongUri(uri: Uri): Boolean {
            return uri.authority == "nosong.ostatic"
        }

    }

    @Throws(IOException::class)
    override fun open(dataSpec: DataSpec): Long {

        Log.d(TAG, "Opening ${dataSpec.uri}")

        updatedDataSpec = dataSpec
        val isSongAvailable: Boolean = !isNoSongUri(dataSpec.uri)

        if (!isSongAvailable) {
            Log.d(TAG, "Song is not available, will be fetched from network or cache")
            val songUrl = repo.getSongMp3Url(dataSpec.uri.path as String)
            updatedDataSpec = DataSpec(
                Uri.parse(songUrl),
                dataSpec.httpMethod,
                dataSpec.httpBody,
                dataSpec.absoluteStreamPosition,
                dataSpec.position,
                dataSpec.length,
                dataSpec.key,
                dataSpec.flags
            )

        } else {
            Log.d(TAG, "Song is available, starting streaming")
        }

        return realDataSource.open(updatedDataSpec)
    }

    @Throws(IOException::class)
    override fun read(buffer: ByteArray, offset: Int, readLength: Int): Int {
        return realDataSource.read(buffer, offset, readLength)
    }

    @Throws(IOException::class)
    override fun close() {
        realDataSource.close()
        updatedDataSpec = null
    }

    override fun getUri(): Uri? {
        return updatedDataSpec?.uri
    }

    override fun addTransferListener(transferListener: TransferListener?) {
        //do noting
    }

}