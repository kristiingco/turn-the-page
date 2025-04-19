package edu.utexas.turnthepage.goals

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import edu.utexas.turnthepage.databinding.FragmentGoalsBinding
import edu.utexas.turnthepage.model.BookStatus
import edu.utexas.turnthepage.model.Goal
import edu.utexas.turnthepage.repository.FirestoreRepository

class GoalsFragment : Fragment() {
    private lateinit var binding: FragmentGoalsBinding
    private val repo = FirestoreRepository()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid ?: "anonymous"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentGoalsBinding.inflate(inflater, container, false)

        loadGoal()
        loadProgress()

        binding.saveGoalButton.setOnClickListener {
            val target = binding.goalInput.text.toString().toIntOrNull() ?: 0
            val goal = Goal(userId, target)
            repo.setGoal(goal) { success ->
                val msg = if (success) "Goal saved!" else "Failed to save"
                Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                if (success) loadProgress()
            }
        }

        return binding.root
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
            val finished = booksWithStatus.count { it.second == BookStatus.FINISHED }
            binding.finishedCount.text = "Books finished: $finished"

            repo.getGoal { goal ->
                val target = goal?.targetCount ?: 0
                if (target > 0) {
                    val percent = (finished * 100 / target).coerceAtMost(100)
                    binding.goalProgress.progress = percent
                }
            }
        }
    }
}
