package edu.uta.turnthepage.books

import edu.utexas.turnthepage.model.Book
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class BookRepository {
    private val api: BookApi

    init {
        val retrofit = Retrofit.Builder()
            .baseUrl(BookApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(BookApi::class.java)
    }

    suspend fun searchBooks(query: String): List<Book> = withContext(Dispatchers.IO) {
        try {
            val response = api.searchBooks(query)
            response.docs.map {
                Book(
                    title = it.title ?: "Untitled",
                    author = it.author_name?.joinToString(", ") ?: "Unknown Author",
                    coverId = it.cover_i
                )
            }
        } catch (e: Exception) {
            emptyList() // Handle failure safely
        }
    }
}
