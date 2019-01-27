package io.github.artenes.ostatic.view

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.artenes.ostatic.OstaticApplication
import io.github.artenes.ostatic.R
import io.github.artenes.ostatic.db.TopAlbumView
import kotlinx.android.synthetic.main.search_fragment.view.*
import kotlinx.coroutines.*

class SearchFragment : Fragment(), TextView.OnEditorActionListener, AlbumsAdapter.OnAlbumClickListener {

    val adapter = AlbumsAdapter(this)
    val repo = OstaticApplication.REPOSITORY
    val job = Job()
    val scope = CoroutineScope(Dispatchers.Main + job)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.search_fragment, container, false)
        view.searchQuery.setOnEditorActionListener(this)
        view.resultList.adapter = adapter
        view.resultList.layoutManager = LinearLayoutManager(requireContext())
        view.progressBar.visibility = View.GONE
        return view
    }

    fun search(query: String) = scope.launch {
        val view = this@SearchFragment.view as View

        view.progressBar.visibility = View.VISIBLE
        view.resultList.visibility = View.GONE

        val results = withContext(Dispatchers.IO) {
            repo.searchAlbum(query)
        }
        adapter.setData(results)

        view.progressBar.visibility = View.GONE
        view.resultList.visibility = View.VISIBLE
    }

    fun openAlbum(albumId: String) {
        AlbumActivity.start(requireContext(), albumId)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            search(v?.text.toString())
            return true
        }
        return false
    }

    override fun onAlbumClicked(album: TopAlbumView) {
        openAlbum(album.id)
    }

}