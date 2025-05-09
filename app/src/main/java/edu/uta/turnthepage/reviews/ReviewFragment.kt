package edu.utexas.turnthepage.reviews

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import edu.utexas.turnthepage.R
import edu.utexas.turnthepage.databinding.FragmentReviewBinding
import edu.utexas.turnthepage.model.Review
import edu.utexas.turnthepage.repository.FirestoreRepository
import edu.utexas.turnthepage.viewmodel.BookViewModel

class ReviewFragment : Fragment() {
    private lateinit var binding: FragmentReviewBinding
    private val viewModel: BookViewModel by activityViewModels()
    private val firestoreRepo = FirestoreRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentReviewBinding.inflate(inflater, container, false)

        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return binding.root
        val book = viewModel.selectedBook.value ?: return binding.root

        firestoreRepo.getUserReviewForBook(book, userId) { existingReview ->
            existingReview?.let {
                binding.ratingBar.rating = it.rating.toFloat()
                binding.commentInput.setText(it.comment)
            }

            binding.submitReviewButton.setOnClickListener {
                val rating = binding.ratingBar.rating.toInt()
                val comment = binding.commentInput.text.toString().trim()

                val review = Review(
                    userId = userId,
                    bookTitle = book.title,
                    bookAuthor = book.author,
                    rating = rating,
                    comment = comment,
                    timestamp = System.currentTimeMillis()
                )

                firestoreRepo.submitReview(review) { success ->
                    val msg = if (success) "Review updated!" else "Failed to submit"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    if (success) requireActivity().onBackPressedDispatcher.onBackPressed()
                }
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
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }
            R.id.action_logout -> {
                FirebaseAuth.getInstance().signOut()
                findNavController().navigate(R.id.loginFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
