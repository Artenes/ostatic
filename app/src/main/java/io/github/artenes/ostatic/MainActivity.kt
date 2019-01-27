package io.github.artenes.ostatic

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import io.github.artenes.ostatic.view.HomeFragment
import io.github.artenes.ostatic.view.LibraryFragment
import io.github.artenes.ostatic.view.SearchFragment
import kotlinx.android.synthetic.main.main_activity.*

class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        bottomNavigationView.setOnNavigationItemSelectedListener(this)
        changeToHomeTab()
    }

    fun changeToHomeTab() {
        replaceFragment(HomeFragment())
    }

    fun changeToSearchTab() {
        replaceFragment(SearchFragment())
    }

    fun changeToLibraryTab() {
        replaceFragment(LibraryFragment())
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_home -> changeToHomeTab()
            R.id.action_search -> changeToSearchTab()
            R.id.action_library -> changeToLibraryTab()
        }
        return true
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(R.id.content, fragment).commit()
    }

}