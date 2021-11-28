package com.he1io.s4cproject.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.he1io.s4cproject.ui.viewmodel.FirestoreViewModel
import com.he1io.s4cproject.R
import com.he1io.s4cproject.data.local.S4CApplication
import com.he1io.s4cproject.data.model.SocialAction
import com.he1io.s4cproject.databinding.FragmentSocialActionAddBinding
import com.he1io.s4cproject.ui.viewmodel.RoomViewModel
import com.he1io.s4cproject.ui.viewmodel.RoomViewModelFactory
import com.he1io.s4cproject.util.Mode
import kotlinx.coroutines.launch

class SocialActionAddFragment : Fragment() {

    private val navigationArgs: SocialActionAddFragmentArgs by navArgs()

    private var _binding: FragmentSocialActionAddBinding? = null
    private val binding get() = _binding!!

    private val firestoreViewModel = FirestoreViewModel()
    private val roomViewModel: RoomViewModel by activityViewModels {
        RoomViewModelFactory(
            (activity?.application as S4CApplication).database.socialActionDao()
        )
    }

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
        mode = if (socialActionId.isBlank()) Mode.INSERT else Mode.UPDATE

        if (mode == Mode.UPDATE) {
            firestoreViewModel.getSocialActionById(socialActionId).observe(viewLifecycleOwner) {
                bind(it)
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
        var isValid = true

        when {
            binding.etSocialActionName.text.toString().isBlank() -> {
                binding.etSocialActionName.error = getString(R.string.required_field_error)
                isValid = false
            }

            binding.etSocialActionYear.text.toString().isBlank() -> {
                binding.etSocialActionYear.error = getString(R.string.required_field_error)
                isValid = false
            }

            binding.etSocialActionMode.text.toString().isBlank() -> {
                binding.etSocialActionMode.error = getString(R.string.required_field_error)
                isValid = false
            }

            binding.etSocialActionProject.text.toString().isBlank() -> {
                binding.etSocialActionProject.error = getString(R.string.required_field_error)
                isValid = false
            }

            binding.etSocialActionSubvention.text.toString().isBlank() -> {
                binding.etSocialActionSubvention.error = getString(R.string.required_field_error)
                isValid = false
            }

            binding.etSocialActionSpending.text.toString().isBlank() -> {
                binding.etSocialActionSpending.error = getString(R.string.required_field_error)
                isValid = false
            }

            binding.etSocialActionCountry.text.toString().isBlank() -> {
                binding.etSocialActionCountry.error = getString(R.string.required_field_error)
                isValid = false
            }

            binding.etSocialActionRegion.text.toString().isBlank() -> {
                binding.etSocialActionRegion.error = getString(R.string.required_field_error)
                isValid = false
            }

            binding.etSocialActionAdministration.text.toString().isBlank() -> {
                binding.etSocialActionAdministration.error = getString(R.string.required_field_error)
                isValid = false
            }
        }

        return isValid
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
                this.findNavController().navigateUp()
            }.show()
    }

    private fun parseSocialAction(): SocialAction {
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
}
