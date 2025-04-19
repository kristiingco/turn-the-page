package edu.utexas.turnthepage.books

import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import edu.utexas.turnthepage.R
import edu.utexas.turnthepage.databinding.FragmentMyBooksBinding
import edu.utexas.turnthepage.model.Book
import edu.utexas.turnthepage.model.BookStatus
import edu.utexas.turnthepage.repository.FirestoreRepository
import edu.utexas.turnthepage.viewmodel.BookViewModel

class MyBooksFragment : Fragment() {
    private lateinit var binding: FragmentMyBooksBinding
    private lateinit var adapter: BookSearchAdapter // reuse existing one
    private val repo = FirestoreRepository()
    private var allBooks: List<Pair<Book, BookStatus>> = emptyList()
    private var currentFilter: BookStatus? = null
    private val viewModel: BookViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentMyBooksBinding.inflate(inflater, container, false)

        adapter = BookSearchAdapter { selectedBook ->
            viewModel.selectBook(selectedBook)
            findNavController().navigate(R.id.action_myBooksFragment_to_bookDetailFragment)
        }

        binding.myBooksRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.myBooksRecycler.adapter = adapter

        setupFilters()
        fetchBooks()

        return binding.root
    }

    private fun setupFilters() {
        fun setup(button: Button, status: BookStatus?) {
            button.setOnClickListener {
                currentFilter = status
                applyFilter()
            }
        }

        setup(binding.filterToRead, BookStatus.TO_READ)
        setup(binding.filterReading, BookStatus.READING)
        setup(binding.filterFinished, BookStatus.FINISHED)
        setup(binding.filterDNF, BookStatus.DID_NOT_FINISH)
    }

    private fun fetchBooks() {
        repo.getUserBooks { booksWithStatus ->
            allBooks = booksWithStatus
            applyFilter()
        }
    }

    private fun applyFilter() {
        val filtered = currentFilter?.let { status ->
            allBooks.filter { it.second == status }.map { it.first }
        } ?: allBooks.map { it.first }

        adapter.submitList(filtered)
    }
}
