package com.he1io.s4cproject.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.he1io.s4cproject.ui.viewmodel.FirestoreViewModel
import com.he1io.s4cproject.R
import com.he1io.s4cproject.data.model.SocialAction
import com.he1io.s4cproject.databinding.FragmentSocialActionAddBinding

class SocialActionAddFragment : Fragment() {

    private var _binding: FragmentSocialActionAddBinding? = null
    private val binding get() = _binding!!

    val db = Firebase.firestore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentSocialActionAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply()
        {
            fabSaveSocialAction.setOnClickListener {
               if (isSocialActionValid()){
                   showSaveConfirmationDialog()
               }
            }
        }
    }


    private fun isSocialActionValid(): Boolean {
        //TODO: Comprobar todas las validaciones
        return true
    }


    private fun showSaveConfirmationDialog() {

        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.save_question))
            .setCancelable(false)
            .setNegativeButton("Cancelar") { _, _ -> }
            .setPositiveButton("Confirmar") { _, _ ->
                val firestoreViewModel = FirestoreViewModel()
                firestoreViewModel.saveSocialActionToFirebase(bindSocialAction())
                // saveSocialAction(bindSocialAction())
                Toast.makeText(requireContext(), getString(R.string.add_social_action_message), Toast.LENGTH_SHORT).show()
                this.findNavController().navigateUp()
            }.show()
    }

    private fun bindSocialAction(): SocialAction {
        //TODO: Qué hacer con la ID? Se generará sola pero la tendré que guardar para poder hacer GET
        binding.apply {
            return SocialAction(
                etSocialActionName.text.toString(),
                etSocialActionYear.text.toString().toInt(),
                etSocialActionMode.text.toString(),
                etSocialActionProject.text.toString(),
                etSocialActionSubvention.text.toString().toDouble(),
                etSocialActionSpending.text.toString().toDouble(),
                etSocialActionCountry.text.toString(),
                etSocialActionRegion.text.toString(),
                etSocialActionAdministration.text.toString()
            )
        }
    }

    private fun saveSocialAction(socialAction: SocialAction){
        db.collection("social_action")
            .add(socialAction)
            .addOnSuccessListener { documentReference ->
                socialAction.id = documentReference.id
            }
    }
}
