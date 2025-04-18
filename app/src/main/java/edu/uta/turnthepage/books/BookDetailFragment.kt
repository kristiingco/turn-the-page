package edu.utexas.turnthepage.books

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import edu.utexas.turnthepage.databinding.FragmentBookDetailBinding
import edu.utexas.turnthepage.viewmodel.BookViewModel

class BookDetailFragment : Fragment() {
    private lateinit var binding: FragmentBookDetailBinding
    private val viewModel: BookViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBookDetailBinding.inflate(inflater, container, false)

        viewModel.selectedBook.value?.let { book ->
            binding.bookTitle.text = book.title
            binding.bookAuthor.text = book.author
            val coverUrl = book.getCoverUrl()
            Glide.with(this)
                .load(coverUrl)
                .into(binding.bookCover)
        }

        return binding.root
    }
}
