package io.github.artenes.ostatic.view

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.artenes.ostatic.R

class AlbumsActivity : AppCompatActivity() {

    companion object {

        const val TITLE = "title"

        fun start(context: Context, title: String, category: String) {
            val intent = Intent(context, AlbumsActivity::class.java)
            intent.putExtra(TITLE, title)
            intent.putExtra(AlbumsFragment.CATEGORY, category)
            context.startActivity(intent)
        }

    }

    val newTitle: String by lazy {
        intent?.getStringExtra(TITLE) ?: ""
    }

    val category: String by lazy {
        intent?.getStringExtra(AlbumsFragment.CATEGORY) ?: ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_activity)
        title = newTitle
        supportFragmentManager.beginTransaction().replace(R.id.content, AlbumsFragment.make(category)).commit()
    }

}