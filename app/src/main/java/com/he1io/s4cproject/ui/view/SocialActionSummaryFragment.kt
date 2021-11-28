package com.he1io.s4cproject.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.viewModelScope
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
import com.he1io.s4cproject.data.local.S4CApplication
import com.he1io.s4cproject.data.model.SocialAction
import com.he1io.s4cproject.databinding.FragmentSocialActionSummaryBinding
import com.he1io.s4cproject.databinding.LayoutBottomSheetBinding
import com.he1io.s4cproject.ui.viewmodel.RoomViewModel
import com.he1io.s4cproject.ui.viewmodel.RoomViewModelFactory
import kotlinx.coroutines.launch

class SocialActionSummaryFragment : Fragment() {

    private var _binding: FragmentSocialActionSummaryBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private val firestoreViewModel = FirestoreViewModel()

    private val roomViewModel: RoomViewModel by activityViewModels {
        RoomViewModelFactory(
            (activity?.application as S4CApplication).database.socialActionDao()
        )
    }

    private lateinit var adapter: CustomAdapter


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
            binding.btLogin.text = getString(R.string.logout_button)
        }

        // Mostrar los datos guardados en caché observando actualizaciones
        roomViewModel.retrieveSocialActionsList().observe(this.viewLifecycleOwner){
            adapter = CustomAdapter(it) { currentAction ->
                showBottomSheetDialog(currentAction)
            }
            binding.rvSocialAction.adapter = adapter
            binding.rvSocialAction.layoutManager = LinearLayoutManager(context)
        }

        // Cuando haya actualizaciones en Firebase, actualizar el caché
        // val adapter = SocialActionListAdapter()
        firestoreViewModel.getSavedSocialActions().observe(this.viewLifecycleOwner) {
            // adapter.submitList(it)
            /*
            adapter = CustomAdapter(it) { currentAction ->
                showBottomSheetDialog(currentAction)
            }
            binding.rvSocialAction.adapter = adapter
            binding.rvSocialAction.layoutManager = LinearLayoutManager(context)*/
            /* TODO:    Evidentemente esto no puede hacerse así
                        Siempre que haya un cambio borrar toda la BD y volver a crearla con los datos en la nube no es nada óptimo
                        Habría que implementar una lógica para que la sincronización entre remoto y local sea óptima
             */

            roomViewModel.insertReplacingAllSocialAction(it)

        }

        binding.apply {
            // rvSocialAction.adapter = adapter
            // rvSocialAction.layoutManager = LinearLayoutManager(context)
            btLogin.setOnClickListener {
                if (currentUser != null) {
                    auth.signOut()
                    currentUser = null
                    btLogin.text = getString(R.string.login_button)
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

    private fun goToAddSocialActionFragment(socialActionId: String) {
        val action =
            SocialActionSummaryFragmentDirections.actionSocialActionSummaryFragmentToSocialActionAddFragment(
                socialActionId
            )
        findNavController().navigate(action)
    }

    private fun showDeleteConfirmationDialog(socialAction: SocialAction) {

        MaterialAlertDialogBuilder(requireContext())
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton("Cancelar") { _, _ -> }
            .setPositiveButton("Confirmar") { _, _ ->
                firestoreViewModel.deleteSocialAction(socialAction.id)
                Toast.makeText(
                    requireContext(),
                    getString(R.string.delete_social_action_message),
                    Toast.LENGTH_SHORT
                ).show()
            }.show()
    }

    private fun showBottomSheetDialog(currentAction: SocialAction) {
        val bottomSheetDialog = BottomSheetDialog(
            requireActivity(),
            R.style.BottomSheetDialogTheme
        )

        val bottomSheetView = LayoutBottomSheetBinding.inflate(LayoutInflater.from(context))
        bottomSheetDialog.setContentView(bottomSheetView.root)
        bottomSheetDialog.show()

        bottomSheetView.apply {
            tvSocialActionName.text = currentAction.name
            tvSocialActionYear.text = currentAction.year.toString()
            tvSocialActionMode.text = currentAction.mode
            tvSocialActionAddress.text = currentAction.getAddressFormatted()
            btEdit.setOnClickListener {
                goToAddSocialActionFragment(currentAction.id)
                bottomSheetDialog.dismiss()
            }
            btDelete.setOnClickListener {
                showDeleteConfirmationDialog(currentAction)
                bottomSheetDialog.dismiss()
            }
        }
    }
}