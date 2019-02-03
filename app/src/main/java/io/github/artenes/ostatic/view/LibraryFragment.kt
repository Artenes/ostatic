package io.github.artenes.ostatic.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import io.github.artenes.ostatic.R
import kotlinx.android.synthetic.main.library_fragment.view.*

class LibraryFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.library_fragment, container, false)

        val context = container?.context?.applicationContext as Context
        view.pager.adapter = LibraryPageAdapter(context, childFragmentManager)

        return view
    }

}