package edu.utexas.turnthepage.books

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import edu.utexas.turnthepage.databinding.FragmentBookDetailBinding
import edu.utexas.turnthepage.model.BookStatus
import edu.utexas.turnthepage.repository.FirestoreRepository
import edu.utexas.turnthepage.viewmodel.BookViewModel

class BookDetailFragment : Fragment() {
    private lateinit var binding: FragmentBookDetailBinding
    private val viewModel: BookViewModel by activityViewModels()
    private val firestoreRepo = FirestoreRepository()

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

        binding.addToListButton.setOnClickListener {
            showStatusDialog()
        }


        return binding.root
    }

    private fun saveBookStatus(status: BookStatus) {
        val book = viewModel.selectedBook.value ?: return

        firestoreRepo.saveBookStatus(book, status) { success ->
            val msg = if (success) "Saved as ${status.name}" else "Failed to save status"
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }
    }


    private fun showStatusDialog() {
        val options = arrayOf("To Read", "Reading", "Finished", "Did Not Finish")
        val statusValues = BookStatus.values()

        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Set Book Status")
            .setItems(options) { _, which ->
                val selectedStatus = statusValues[which]
                saveBookStatus(selectedStatus)
            }
            .show()
    }

}
