package com.he1io.s4cproject.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.he1io.s4cproject.ui.viewmodel.FirestoreViewModel
import com.he1io.s4cproject.R
import com.he1io.s4cproject.data.model.SocialAction
import com.he1io.s4cproject.databinding.FragmentSocialActionAddBinding
import com.he1io.s4cproject.util.Mode
import kotlinx.coroutines.launch

class SocialActionAddFragment : Fragment() {

    companion object {
        private const val TAG = "SocialActionAddFragment"
    }

    private val navigationArgs: SocialActionAddFragmentArgs by navArgs()

    private var _binding: FragmentSocialActionAddBinding? = null
    private val binding get() = _binding!!

    private val db = Firebase.firestore

    val firestoreViewModel = FirestoreViewModel()

    private lateinit var mode: Mode

    private lateinit var socialActionId: String
    private lateinit var socialAction: SocialAction

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

        socialActionId = navigationArgs.socialActionId
        mode = if (socialActionId.isNullOrBlank()) Mode.INSERT else Mode.UPDATE

        when (mode) {
            Mode.UPDATE -> {
                firestoreViewModel.getSocialActionById(socialActionId).observe(viewLifecycleOwner) {
                    bind(it)
                }
            }

            Mode.INSERT -> {

            }
            else -> {

            }
        }
        binding.apply()
        {
            fabSaveSocialAction.setOnClickListener {
                if (isSocialActionValid()) {
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
                when (mode) {
                    Mode.UPDATE -> {
                        socialAction = parseSocialAction()
                        socialAction.id = socialActionId
                        firestoreViewModel.editSocialAction(socialAction)
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.edit_social_action_message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    Mode.INSERT -> {
                        firestoreViewModel.saveSocialActionToFirebase(parseSocialAction())
                        Toast.makeText(
                            requireContext(),
                            getString(R.string.add_social_action_message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                // saveSocialAction(bindSocialAction())
                this.findNavController().navigateUp()
            }.show()
    }

    private fun parseSocialAction(): SocialAction {
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

    private fun bind(socialAction: SocialAction) {
        binding.apply {
            etSocialActionName.setText(socialAction.name)
            etSocialActionYear.setText(socialAction.year.toString())
            etSocialActionMode.setText(socialAction.mode)
            etSocialActionProject.setText(socialAction.project)
            etSocialActionSubvention.setText(socialAction.subvention.toString())
            etSocialActionSpending.setText(socialAction.spending.toString())
            etSocialActionCountry.setText(socialAction.country)
            etSocialActionRegion.setText(socialAction.region)
            etSocialActionAdministration.setText(socialAction.administration)
        }
    }

    private fun saveSocialAction(socialAction: SocialAction) {
        db.collection("social_action")
            .add(socialAction)
            .addOnSuccessListener { documentReference ->
                socialAction.id = documentReference.id
            }
    }
}
