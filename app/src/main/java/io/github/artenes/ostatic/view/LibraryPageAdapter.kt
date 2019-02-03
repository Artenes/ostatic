package io.github.artenes.ostatic.view

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import io.github.artenes.ostatic.R

class LibraryPageAdapter(context: Context, fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private val pages = listOf(
        LibraryFragment(context.getString(R.string.my_albums), FavoriteAlbumsFragment()),
        LibraryFragment(context.getString(R.string.my_songs), FavoriteSongsFragment())
    )

    override fun getItem(position: Int): Fragment {
        return pages[position].fragment
    }

    override fun getCount(): Int {
        return pages.count()
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return pages[position].title
    }

    data class LibraryFragment(val title: String, val fragment: Fragment)

}