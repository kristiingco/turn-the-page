package edu.utexas.turnthepage.goals

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import edu.utexas.turnthepage.R
import edu.utexas.turnthepage.books.BookSearchAdapter
import edu.utexas.turnthepage.databinding.FragmentGoalsBinding
import edu.utexas.turnthepage.model.BookStatus
import edu.utexas.turnthepage.model.Goal
import edu.utexas.turnthepage.repository.FirestoreRepository
import edu.utexas.turnthepage.viewmodel.BookViewModel

class GoalsFragment : Fragment() {
    private lateinit var binding: FragmentGoalsBinding
    private val repo = FirestoreRepository()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous"
    private lateinit var finishedAdapter: BookSearchAdapter
    private val viewModel: BookViewModel by activityViewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentGoalsBinding.inflate(inflater, container, false)

        loadGoal()
        loadProgress()

        binding.saveGoalButton.setOnClickListener {
            val target = binding.goalInput.text.toString().toIntOrNull() ?: 0
            val currentUser = FirebaseAuth.getInstance().currentUser

            if (currentUser != null && target > 0) {
                val goal = Goal(
                    userId = currentUser.uid,
                    targetCount = target,
                    year = 2025 // Optional: Make this dynamic later
                )

                repo.setGoal(goal) { success ->
                    val msg = if (success) "Goal saved!" else "Failed to save"
                    Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    if (success) loadProgress()
                }
            } else {
                Toast.makeText(requireContext(), "Invalid goal or user not logged in", Toast.LENGTH_SHORT).show()
            }
        }


        finishedAdapter = BookSearchAdapter { selectedBook ->
            viewModel.selectBook(selectedBook)
            findNavController().navigate(R.id.action_goalsFragment_to_bookDetailFragment)
        }
        binding.finishedBooksRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.finishedBooksRecycler.adapter = finishedAdapter

        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(false)
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
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadGoal() {
        repo.getGoal { goal ->
            goal?.let {
                binding.goalInput.setText(it.targetCount.toString())
                binding.goalStatus.text = "Goal: ${it.targetCount} books"
            }
        }
    }

    private fun loadProgress() {
        repo.getUserBooks { booksWithStatus ->
            val finishedBooks = booksWithStatus
                .filter { it.second == BookStatus.FINISHED }
                .map { it.first }

            binding.finishedCount.text = "Books finished: ${finishedBooks.size}"
            finishedAdapter.submitList(finishedBooks)

            repo.getGoal { goal ->
                val target = goal?.targetCount ?: 0
                if (target > 0) {
                    val percent = (finishedBooks.size * 100 / target).coerceAtMost(100)
                    binding.goalProgress.progress = percent
                }
            }
        }
    }

}
