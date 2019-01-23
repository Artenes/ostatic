package io.github.artenes.ostatic.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import io.github.artenes.ostatic.R

import kotlinx.android.synthetic.main.list.view.*

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val context: Context = container?.context!!

        val view = inflater.inflate(R.layout.list, container, false)

        val adapter = TopAlbumsAdapter()
        view.mainList.layoutManager = LinearLayoutManager(context)
        view.mainList.adapter = adapter

        val sections = mutableListOf<AlbumSection>()

        val top40 = listOf(
            Album("Persona 5", "http://66.90.93.122/ost/persona-5/Cover.jpg"),
            Album("Need for Speed - Most Wanted", "http://66.90.93.122/ost/need-for-speed-most-wanted/3407-yorciclacq.jpg"),
            Album("Minecraft", "http://66.90.93.122/ost/minecraft/cover.jpg"),
            Album("Super Mario World Original Soundtrack", "http://66.90.93.122/ost/super-mario-world-original-soundtrack/4955-bsqzsatpcp.jpg"),
            Album("Super Smash Bros Brawl - Gamerip", "http://66.90.93.122/ost/super-smash-bros-brawl-gamerip/maxresdefault.jpg"),
            Album("Legend of Zelda, The - Breath of the Wild", "http://66.90.93.122/ost/the-legend-of-zelda-breath-of-the-wild/index.jpg"),
            Album("Legend of Zelda, The - Ocarina of Time Original Soundtrack", "http://66.90.93.122/ost/legend-of-zelda-ocarina-of-time-original-sound-track/2779-grthilgkiv.jpg"),
            Album("Super Mario Bros", "http://66.90.93.122/ost/super-mario-bros/index.png"))

        sections.add(AlbumSection("Top 40 soundtracks", "TOP 40 soundtracks for week #3", top40, true))

        val newly = listOf(
            Album("Need for Speed - Most Wanted", "http://66.90.93.122/ost/need-for-speed-most-wanted/3407-yorciclacq.jpg"),
            Album("Super Smash Bros Brawl - Gamerip", "http://66.90.93.122/ost/super-smash-bros-brawl-gamerip/maxresdefault.jpg"),
            Album("Legend of Zelda, The - Ocarina of Time Original Soundtrack", "http://66.90.93.122/ost/legend-of-zelda-ocarina-of-time-original-sound-track/2779-grthilgkiv.jpg"),
            Album("Persona 5", "http://66.90.93.122/ost/persona-5/Cover.jpg"),
            Album("Super Mario Bros", "http://66.90.93.122/ost/super-mario-bros/index.png"),
            Album("Minecraft", "http://66.90.93.122/ost/minecraft/cover.jpg"),
            Album("Super Mario World Original Soundtrack", "http://66.90.93.122/ost/super-mario-world-original-soundtrack/4955-bsqzsatpcp.jpg"),
            Album("Legend of Zelda, The - Breath of the Wild", "http://66.90.93.122/ost/the-legend-of-zelda-breath-of-the-wild/index.jpg")
        )

        sections.add(AlbumSection("Top 100 newly added soundtracks", "Latest songs from our users", newly, false))

        val months = listOf(
            Album("Super Mario World Original Soundtrack", "http://66.90.93.122/ost/super-mario-world-original-soundtrack/4955-bsqzsatpcp.jpg"),
            Album("Super Smash Bros Brawl - Gamerip", "http://66.90.93.122/ost/super-smash-bros-brawl-gamerip/maxresdefault.jpg"),
            Album("Persona 5", "http://66.90.93.122/ost/persona-5/Cover.jpg"),
            Album("Need for Speed - Most Wanted", "http://66.90.93.122/ost/need-for-speed-most-wanted/3407-yorciclacq.jpg"),
            Album("Legend of Zelda, The - Breath of the Wild", "http://66.90.93.122/ost/the-legend-of-zelda-breath-of-the-wild/index.jpg"),
            Album("Minecraft", "http://66.90.93.122/ost/minecraft/cover.jpg"),
            Album("Super Mario Bros", "http://66.90.93.122/ost/super-mario-bros/index.png"),
            Album("Legend of Zelda, The - Ocarina of Time Original Soundtrack", "http://66.90.93.122/ost/legend-of-zelda-ocarina-of-time-original-sound-track/2779-grthilgkiv.jpg")
        )

        sections.add(AlbumSection("Last 6 months top 100 soundtracks", "Most popular from last 6 months", months, false))

        val year = listOf(
            Album("Super Smash Bros Brawl - Gamerip", "http://66.90.93.122/ost/super-smash-bros-brawl-gamerip/maxresdefault.jpg"),
            Album("Persona 5", "http://66.90.93.122/ost/persona-5/Cover.jpg"),
            Album("Super Mario World Original Soundtrack", "http://66.90.93.122/ost/super-mario-world-original-soundtrack/4955-bsqzsatpcp.jpg"),
            Album("Legend of Zelda, The - Ocarina of Time Original Soundtrack", "http://66.90.93.122/ost/legend-of-zelda-ocarina-of-time-original-sound-track/2779-grthilgkiv.jpg"),
            Album("Minecraft", "http://66.90.93.122/ost/minecraft/cover.jpg"),
            Album("Need for Speed - Most Wanted", "http://66.90.93.122/ost/need-for-speed-most-wanted/3407-yorciclacq.jpg"),
            Album("Legend of Zelda, The - Breath of the Wild", "http://66.90.93.122/ost/the-legend-of-zelda-breath-of-the-wild/index.jpg"),
            Album("Super Mario Bros", "http://66.90.93.122/ost/super-mario-bros/index.png")
        )

        sections.add(AlbumSection("All time top 100 soundtracks", "Best game songs of all time", year, false))

        adapter.setData(sections)

        return view
    }

}