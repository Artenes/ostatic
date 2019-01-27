package io.github.artenes.ostatic.view

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
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
        showLoadingView()

        val results = withContext(Dispatchers.IO) {
            repo.searchAlbum(query)
        }

        if (results.isEmpty()) {
            showNoResultsView()
        } else {
            showResults(results)
        }
    }

    fun showLoadingView() {
        view?.progressBar?.visibility = View.VISIBLE
        view?.resultList?.visibility = View.GONE
        view?.queryStatus?.visibility = View.GONE
    }

    fun showNoResultsView() {
        view?.progressBar?.visibility = View.GONE
        view?.queryStatus?.visibility = View.VISIBLE
        view?.queryStatus?.text = getString(R.string.no_games_found)
        view?.resultList?.visibility = View.GONE
    }

    fun showResults(results: List<TopAlbumView>) {
        hideKeyboard()
        view?.progressBar?.visibility = View.GONE
        view?.queryStatus?.visibility = View.GONE
        view?.resultList?.visibility = View.VISIBLE
        adapter.setData(results)
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

    private fun hideKeyboard() {
        val imm = requireContext().getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view?.queryStatus?.windowToken, 0)
    }

}