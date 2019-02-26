package io.github.artenes.ostatic.model

class AlbumCategory {

    companion object {
        const val CATEGORY_RECENT = "recent"
        const val CATEGORY_TOP_40 = "top_40"
        const val CATEGORY_TOP_6_MONTHS = "top_6_months"
        const val CATEGORY_TOP_NEWLY = "top_newly"
        const val CATEGORY_TOP_ALL = "top_all"
        const val CATEGORY_FAVORITES = "favorites"
    }

}

data class AlbumWithCategory(val id: String, val name: String, val cover: String, val category: String)

data class AlbumForShowcase(val id: String, val name: String, val cover: String) {

    fun isCategory(): Boolean {
        return when(id) {
            AlbumCategory.CATEGORY_RECENT -> true
            AlbumCategory.CATEGORY_TOP_40 -> true
            AlbumCategory.CATEGORY_TOP_6_MONTHS -> true
            AlbumCategory.CATEGORY_TOP_ALL -> true
            AlbumCategory.CATEGORY_TOP_NEWLY -> true
            else -> false
        }
    }

}

data class AlbumSection(val title: String, val subtitle: String, val albums: List<AlbumForShowcase>, val isHighlight: Boolean)