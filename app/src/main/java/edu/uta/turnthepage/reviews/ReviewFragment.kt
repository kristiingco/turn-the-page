package edu.utexas.turnthepage.reviews

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.firebase.auth.FirebaseAuth
import edu.utexas.turnthepage.databinding.FragmentReviewBinding
import edu.utexas.turnthepage.model.Review
import edu.utexas.turnthepage.repository.FirestoreRepository
import edu.utexas.turnthepage.viewmodel.BookViewModel

class ReviewFragment : Fragment() {
    private lateinit var binding: FragmentReviewBinding
    private val viewModel: BookViewModel by activityViewModels()
    private val firestoreRepo = FirestoreRepository()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentReviewBinding.inflate(inflater, container, false)

        binding.submitReviewButton.setOnClickListener {
            val rating = binding.ratingBar.rating.toInt()
            val comment = binding.commentInput.text.toString().trim()

            val book = viewModel.selectedBook.value ?: return@setOnClickListener
            val review = Review(
                userId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous",
                bookTitle = book.title,
                bookAuthor = book.author,
                rating = rating,
                comment = comment
            )

            firestoreRepo.submitReview(review) { success ->
                val msg = if (success) "Review submitted!" else "Failed to submit review"
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                if (success) requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        return binding.root
    }
}
