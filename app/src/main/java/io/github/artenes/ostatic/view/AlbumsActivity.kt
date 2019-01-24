package io.github.artenes.ostatic.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.artenes.ostatic.R

class AlbumsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_activity)
        title = getString(R.string.top_40_soundtracks)
        supportFragmentManager.beginTransaction().replace(R.id.content, AlbumsFragment.make(AlbumsFragment.TOP_40)).commit()
    }

}