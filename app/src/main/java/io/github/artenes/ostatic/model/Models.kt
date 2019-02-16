package io.github.artenes.ostatic.model

data class AlbumWithCategory(val id: String, val name: String, val cover: String, val category: String) {

    companion object {
        const val CATEGORY_RECENT = "recent"
        const val CATEGORY_TOP_40 = "top_40"
        const val CATEGORY_TOP_6_MONTHS = "top_6_months"
        const val CATEGORY_TOP_NEWLY = "top_newly"
        const val CATEGORY_TOP_ALL = "top_all"
    }

}