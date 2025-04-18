package edu.utexas.turnthepage.model

data class Book(
    val title: String = "",
    val author: String = "",
    val coverId: Int? = null
) {
    fun getCoverUrl(): String? {
        return coverId?.let { "https://covers.openlibrary.org/b/id/$it-L.jpg" }
    }
}
