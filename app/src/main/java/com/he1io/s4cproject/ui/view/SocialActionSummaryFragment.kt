package com.he1io.s4cproject.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.he1io.s4cproject.util.CustomAdapter
import com.he1io.s4cproject.ui.viewmodel.FirestoreViewModel
import com.he1io.s4cproject.R
import com.he1io.s4cproject.databinding.FragmentSocialActionSummaryBinding
import com.he1io.s4cproject.databinding.LayoutBottomSheetBinding
import com.he1io.s4cproject.util.Mode

class SocialActionSummaryFragment : Fragment() {

    private var _binding: FragmentSocialActionSummaryBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth

    private lateinit var adapter: CustomAdapter

    private val firestoreViewModel = FirestoreViewModel()

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
        if (currentUser != null) {
            binding.btLogin.text = currentUser.email
        }


        // val adapter = SocialActionListAdapter()
        firestoreViewModel.getSavedSocialActions().observe(this.viewLifecycleOwner) {
            // adapter.submitList(it)
            adapter = CustomAdapter(it) {currentAction ->
                val bottomSheetDialog = BottomSheetDialog(
                    requireActivity(),
                    R.style.BottomSheetDialogTheme
                )
                val bottomSheetView = LayoutBottomSheetBinding.inflate(LayoutInflater.from(context))
                bottomSheetDialog.setContentView(bottomSheetView.root)
                bottomSheetDialog.show()

                bottomSheetView.tvSocialActionName.text = currentAction.name
                bottomSheetView.btEdit.setOnClickListener{
                    goToAddSocialActionFragment(currentAction.id)
                    bottomSheetDialog.dismiss()
                }
                bottomSheetView.btDelete.setOnClickListener{
                    showDeleteConfirmationDialog(currentAction.id)
                    bottomSheetDialog.dismiss()
                }

            }
            binding.rvSocialAction.adapter = adapter
            binding.rvSocialAction.layoutManager = LinearLayoutManager(context)
        }

        binding.apply {
            // rvSocialAction.adapter = adapter
            // rvSocialAction.layoutManager = LinearLayoutManager(context)
            btLogin.setOnClickListener {
                if (currentUser != null) {
                    auth.signOut()
                    currentUser = null
                    btLogin.text = getString(R.string.login)
                } else {
                    goToLogInFragment()
                }
            }

            fabAddSocialAction.setOnClickListener {
                goToAddSocialActionFragment("") // ID vacía porque vamos a crear una nueva
            }
        }
    }

    private fun goToLogInFragment() {
        val action =
            SocialActionSummaryFragmentDirections.actionSocialActionSummaryFragmentToLoginDialogFragment()
        findNavController().navigate(action)
    }

    private fun goToAddSocialActionFragment(socialActionId: String){
        val action =
            SocialActionSummaryFragmentDirections.actionSocialActionSummaryFragmentToSocialActionAddFragment(socialActionId)
        findNavController().navigate(action)
    }

    private fun showDeleteConfirmationDialog(socialActionId: String) {

        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton("Cancelar") { _, _ -> }
            .setPositiveButton("Confirmar") { _, _ ->

                        firestoreViewModel.deleteSocialAction(socialActionId)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.delete_social_action_message),
                            Toast.LENGTH_SHORT
                        ).show()

            }.show()
    }
}