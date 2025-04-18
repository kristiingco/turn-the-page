package edu.utexas.turnthepage.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import edu.utexas.turnthepage.R
import edu.utexas.turnthepage.databinding.FragmentRegisterBinding

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()

        binding.registerButton.setOnClickListener {
            val email = binding.emailInput.text.toString().trim()
            val password = binding.passwordInput.text.toString().trim()
            val confirm = binding.confirmPasswordInput.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (password == confirm) {
                    auth.createUserWithEmailAndPassword(email, password)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Account created!", Toast.LENGTH_SHORT).show()
                            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                        }
                        .addOnFailureListener {
                            Toast.makeText(context, "Failed: ${it.message}", Toast.LENGTH_LONG).show()
                        }
                } else {
                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "All fields are required", Toast.LENGTH_SHORT).show()
            }
        }

        binding.toLogin.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        return binding.root
    }
}
