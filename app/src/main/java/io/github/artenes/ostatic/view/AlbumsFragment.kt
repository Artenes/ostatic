package io.github.artenes.ostatic.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.artenes.ostatic.R
import io.github.artenes.ostatic.db.ApplicationDatabase
import io.github.artenes.ostatic.db.TopAlbumView
import kotlinx.android.synthetic.main.preload_list.view.*
import kotlinx.coroutines.*

class AlbumsFragment : Fragment(), AlbumsAdapter.OnAlbumClickListener {

    companion object {

        const val CATEGORY = "CATEGORY"
        const val TOP_40 = "TOP_40"
        const val TOP_ALL = "TOP_ALL"
        const val TOP_LAST = "TOP_LAST"
        const val TOP_NEWLY = "TOP_NEWLY"

        fun make(category: String): AlbumsFragment {
            val fragment = AlbumsFragment()
            val bundle = Bundle()
            bundle.putString(CATEGORY, category)
            fragment.arguments = bundle
            return fragment
        }

    }

    val job = Job()
    val scope = CoroutineScope(Dispatchers.Main + job)
    val category by lazy {
        val args = arguments as Bundle
        args.getString(CATEGORY)
    }

    lateinit var adapter: AlbumsAdapter
    lateinit var db: ApplicationDatabase

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val context: Context = container?.context!!

        val view = inflater.inflate(R.layout.list, container, false)

        adapter = AlbumsAdapter(this)
        view.mainList.layoutManager = LinearLayoutManager(context)
        view.mainList.adapter = adapter

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        db = ApplicationDatabase.getInstance(requireContext())
        loadAndBind()
    }

    fun loadAndBind() = scope.launch {
        val view: View = this@AlbumsFragment.view as View

        view.mainList.visibility = View.GONE
        view.progress.visibility = View.VISIBLE

        val albums = withContext(Dispatchers.IO) {
            when (category) {
                TOP_40 -> db.albumDao().getTop40()
                TOP_ALL -> db.albumDao().getTop100AllTime()
                TOP_LAST -> db.albumDao().getTop100Last6Months()
                TOP_NEWLY -> db.albumDao().getTop100NewlyAdded()
                else -> emptyList()
            }
        }

        adapter.setData(albums)

        view.mainList.visibility = View.VISIBLE
        view.progress.visibility = View.GONE
    }

    override fun onAlbumClicked(album: TopAlbumView) {
        AlbumActivity.start(requireContext(), album.id)
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }

}