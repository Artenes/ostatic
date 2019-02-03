package io.github.artenes.ostatic.view

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.artenes.ostatic.MainActivity
import io.github.artenes.ostatic.OstaticApplication
import io.github.artenes.ostatic.R
import kotlinx.coroutines.*

class SplashScreen: AppCompatActivity() {

    private val job = Job()
    private val scope = CoroutineScope(Dispatchers.Main + job)
    private val repo = OstaticApplication.REPOSITORY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen)
        loadTopAlbums()
    }

    private fun loadTopAlbums() = scope.launch {

        //loads data from server if not available in local database
        //since this take some time the use of a splash screen seems ok
        withContext(Dispatchers.IO) {
            repo.getTop40(1)
            repo.getTop100AllTime(1)
            repo.getTop100Last6Months(1)
            repo.getTop100NewlyAdded(1)
        }

        startActivity(Intent(this@SplashScreen, MainActivity::class.java))
        finish()

    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

}