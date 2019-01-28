package io.github.artenes.ostatic.view

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.fragment.app.Fragment

class AboutFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val webview = WebView(container?.context)
        webview.setBackgroundColor(Color.TRANSPARENT)
        webview.loadUrl("file:///android_asset/about.html")
        return webview
    }

}