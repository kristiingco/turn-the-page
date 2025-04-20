package edu.utexas.turnthepage.books

import android.os.Bundle
import android.view.*
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.firebase.auth.FirebaseAuth
import edu.utexas.turnthepage.R
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

        val book = viewModel.selectedBook.value ?: return binding.root
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return binding.root

        // Set book details
        binding.bookTitle.text = book.title
        binding.bookAuthor.text = book.author
        val coverUrl = book.getCoverUrl()
        Glide.with(this)
            .load(coverUrl)
            .transform(RoundedCorners(16))
            .into(binding.bookCover)

        // Button listeners
        binding.addToListButton.setOnClickListener { showStatusDialog() }
        binding.writeReviewButton.setOnClickListener {
            findNavController().navigate(R.id.action_bookDetailFragment_to_reviewFragment)
        }

        // Fetch and display reviews
        val inflater = LayoutInflater.from(requireContext())

        firestoreRepo.getReviewsForBook(book) { reviews ->
            val userReview = reviews.find { it.userId == userId }
            val otherReviews = reviews.filter { it.userId != userId }

            if (userReview != null) {
                binding.writeReviewButton.text = "Edit Review"
                binding.yourReviewHeader.visibility = View.VISIBLE
                binding.yourReviewContainer.visibility = View.VISIBLE

                val view = inflater.inflate(R.layout.review_item, binding.yourReviewContainer, false)
                view.findViewById<TextView>(R.id.reviewRating).text =
                    "★".repeat(userReview.rating) + "☆".repeat(5 - userReview.rating)
                view.findViewById<TextView>(R.id.reviewComment).text = userReview.comment
                binding.yourReviewContainer.addView(view)
            }

            otherReviews.forEach { review ->
                val view = inflater.inflate(R.layout.review_item, binding.allReviewsContainer, false)
                view.findViewById<TextView>(R.id.reviewRating).text =
                    "★".repeat(review.rating) + "☆".repeat(5 - review.rating)
                view.findViewById<TextView>(R.id.reviewComment).text = review.comment
                binding.allReviewsContainer.addView(view)
            }
        }

        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                findNavController().navigate(R.id.loginFragment)
                true
            }
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
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
