package com.he1io.s4cproject

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.he1io.s4cproject.databinding.FragmentSocialActionSummaryBinding

class SocialActionSummaryFragment : Fragment() {

    private var _binding: FragmentSocialActionSummaryBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentSocialActionSummaryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        var currentUser = auth.currentUser
        // Si el usuario ya está logeado, mostramos su e-mail en vez de "Log in"
        if (currentUser != null){
            binding.btLogin.text = currentUser.email
        }

        binding.apply {
            btLogin.setOnClickListener {
                if (currentUser != null){
                    auth.signOut()
                    currentUser = null
                    btLogin.text = getString(R.string.login)
                } else {
                    val action =
                        SocialActionSummaryFragmentDirections.actionSocialActionSummaryFragmentToLoginDialogFragment()
                    findNavController().navigate(action) }
                }

            fabAddSocialAction.setOnClickListener {
                val action = SocialActionSummaryFragmentDirections.actionSocialActionSummaryFragmentToSocialActionAddFragment()
                findNavController().navigate(action)
            }
        }
    }
}