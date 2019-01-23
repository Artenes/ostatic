package io.github.artenes.ostatic

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import io.github.artenes.ostatic.view.HomeFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction().replace(R.id.content, HomeFragment()).commit()
    }

}
