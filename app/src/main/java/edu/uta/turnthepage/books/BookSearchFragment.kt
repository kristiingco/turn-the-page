package edu.utexas.turnthepage.books

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import edu.utexas.turnthepage.R
import edu.utexas.turnthepage.databinding.FragmentBookSearchBinding
import edu.utexas.turnthepage.viewmodel.BookViewModel

class BookSearchFragment : Fragment() {
    private lateinit var binding: FragmentBookSearchBinding
    private val viewModel: BookViewModel by activityViewModels()
    private lateinit var adapter: BookSearchAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookSearchBinding.inflate(inflater, container, false)

        adapter = BookSearchAdapter { selectedBook ->
            viewModel.selectBook(selectedBook)
            findNavController().navigate(R.id.action_bookSearchFragment_to_bookDetailFragment)
        }

        binding.bookRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.bookRecyclerView.adapter = adapter

        binding.searchInput.setOnEditorActionListener { _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                event?.keyCode == KeyEvent.KEYCODE_ENTER
            ) {
                val query = binding.searchInput.text.toString().trim()
                if (query.isNotEmpty()) {
                    viewModel.searchBooks(query)
                }
                true
            } else {
                false
            }
        }

        viewModel.books.observe(viewLifecycleOwner) { books ->
            adapter.submitList(books)
            if (books.isEmpty()) {
                Toast.makeText(context, "No books found.", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }
}
