package edu.utexas.turnthepage.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import edu.uta.turnthepage.books.BookRepository
import edu.utexas.turnthepage.model.Book
import kotlinx.coroutines.launch

class BookViewModel : ViewModel() {
    private val repository = BookRepository()

    private val _books = MutableLiveData<List<Book>>()
    val books: LiveData<List<Book>> = _books

    fun searchBooks(query: String) {
        viewModelScope.launch {
            val result = repository.searchBooks(query)
            _books.postValue(result)
        }
    }
}
