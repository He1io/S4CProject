package com.he1io.s4cproject

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.he1io.s4cproject.databinding.FragmentLoginDialogBinding

class LoginDialogFragment : Fragment() {

    private var _binding: FragmentLoginDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentLoginDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        auth = Firebase.auth

        binding.apply {
            btLogin.setOnClickListener {
                if (isLogInValid()){
                    auth.signInWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "signInWithEmail:success")
                                val action = LoginDialogFragmentDirections.actionLoginDialogFragmentToSocialActionSummaryFragment2()
                                findNavController().navigate(action)
                            } else {
                                Log.w(TAG, "signInWithEmail:failure", task.exception)
                                Toast.makeText(requireContext(), "Fallo al iniciar sesión, compruebe sus credenciales",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }

            btSignup.setOnClickListener {
                if(isLogInValid()){

                    auth.createUserWithEmailAndPassword(etEmail.text.toString(), etPassword.text.toString())
                        .addOnCompleteListener(requireActivity()) { task ->
                            if (task.isSuccessful) {
                                Log.d(TAG, "createUserWithEmail:success")
                                val action = LoginDialogFragmentDirections.actionLoginDialogFragmentToSocialActionSummaryFragment2()
                                findNavController().navigate(action)
                            } else {
                                //TODO: Check existing user
                                Log.w(TAG, "createUserWithEmail:failure", task.exception)
                                Toast.makeText(requireContext(), "Ha habido un fallo. Inténtelo de nuevo por favor",
                                    Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            }

        }
    }

    private fun isLogInValid(): Boolean {
        var isValid = true

        when {
            binding.etEmail.text.toString().isBlank() -> {
                binding.tilEmail.error = getString(R.string.email_null_error)
                isValid = false
            }

            // Comprobar formato del mail
            !android.util.Patterns.EMAIL_ADDRESS.matcher(binding.etEmail.text.toString()).matches() -> {
                binding.tilEmail.error = getString(R.string.email_bad_format)
                isValid = false
            }

            binding.etPassword.text.toString().isBlank() -> {
                binding.tilPassword.error = getString(R.string.password_null_error)
                isValid = false
            }
            binding.etPassword.length() < 6 -> {
                binding.tilPassword.error = getString(R.string.minimum_characters_error)
                isValid = false
            }
        }

        return isValid
    }
}