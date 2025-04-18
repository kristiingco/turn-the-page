package edu.uta.turnthepage.books

import retrofit2.http.GET
import retrofit2.http.Query

data class OpenLibrarySearchResponse(
    val docs: List<BookDoc>
)

data class BookDoc(
    val title: String?,
    val author_name: List<String>?,
    val cover_i: Int?
)

interface BookApi {
    @GET("search.json")
    suspend fun searchBooks(@Query("q") query: String): OpenLibrarySearchResponse

    companion object {
        const val BASE_URL = "https://openlibrary.org/"
    }
}
