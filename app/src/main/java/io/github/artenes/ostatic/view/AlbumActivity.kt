package io.github.artenes.ostatic.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import io.github.artenes.ostatic.R

class AlbumActivity : AppCompatActivity() {

    companion object {

        const val ALBUM_ID = "ALBUM_ID"

        fun start(context: Context, id: String) {
            val intent = Intent(context, AlbumActivity::class.java)
            intent.putExtra(ALBUM_ID, id)
            context.startActivity(intent)
        }

    }

    val id: String by lazy {
        intent?.getStringExtra(ALBUM_ID) ?: ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_activity)
        supportFragmentManager.beginTransaction().replace(R.id.content, AlbumFragment.make(id)).commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return super.onSupportNavigateUp()
    }

}