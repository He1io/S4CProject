package com.he1io.s4cproject.ui.view

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.ktx.Firebase
import com.he1io.s4cproject.ui.viewmodel.FirestoreViewModel
import com.he1io.s4cproject.R
import com.he1io.s4cproject.data.local.S4CApplication
import com.he1io.s4cproject.data.model.SocialAction
import com.he1io.s4cproject.databinding.FragmentDislpayMapBinding
import com.he1io.s4cproject.databinding.LayoutBottomSheetBinding
import com.he1io.s4cproject.ui.viewmodel.RoomViewModel
import com.he1io.s4cproject.ui.viewmodel.RoomViewModelFactory

class DisplayMapFragment : Fragment() {

    private var _binding: FragmentDislpayMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var auth: FirebaseAuth
    private val firestoreViewModel = FirestoreViewModel()

    private val roomViewModel: RoomViewModel by activityViewModels {
        RoomViewModelFactory(
            (activity?.application as S4CApplication).database.socialActionDao()
        )
    }

    private lateinit var googleMap: GoogleMap
    private val mapZoom = 5f
    private val animationDuration = 4000
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {isGranted ->
            if (isGranted) centerCameraAnimation(googleMap, mapZoom, animationDuration)
        }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        _binding = FragmentDislpayMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = Firebase.auth
        var currentUser = auth.currentUser
        if (currentUser != null) binding.btLogin.text = getString(R.string.logout_button)


        binding.apply {
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
                goToAddSocialActionFragment("") // ID vac??a porque vamos a crear una nueva
            }
        }

        // Mostrar los datos guardados en cach?? observando actualizaciones
        roomViewModel.retrieveSocialActionsList()
            .observe(this.viewLifecycleOwner) { socialActionsList ->
                // TODO:    Toda esta parte del mapa est?? hecha a la carrera, hay que limpiar y encapsular
                //          Tambi??n mirar posibilidades offline porque ahora mismo crashea
                val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync { myMap ->
                    googleMap = myMap
                    googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
                    googleMap.setOnMarkerClickListener { myMarker ->
                        showBottomSheetDialog(myMarker.tag as SocialAction)
                        true
                    }
                    googleMap.clear()

                    centerCameraAnimation(googleMap, mapZoom, animationDuration)

                    if (socialActionsList.isNotEmpty()) {
                        for (socialAction in socialActionsList) {
                            val location = getLocationFromAddress(socialAction.region)
                            googleMap.addMarker(
                                MarkerOptions()
                                    .position(LatLng(location.latitude, location.longitude))
                                    .title(socialAction.name)
                            )!!.tag = socialAction
                        }
                    }
                }
            }

        // Cuando haya actualizaciones en Firebase, actualizar el cach??
        firestoreViewModel.getSavedSocialActions().observe(this.viewLifecycleOwner) {
            /*
                TODO:    Esto no puede hacerse as??
                         Siempre que haya un cambio borrar toda la BD y volver a crearla con los datos en la nube no es nada ??ptimo
                         Habr??a que implementar una l??gica para que la sincronizaci??n entre remoto y local sea ??ptima
             */
            roomViewModel.insertReplacingAllSocialAction(it)
        }
    }

    private fun goToLogInFragment() {
        val action =
            DisplayMapFragmentDirections.actionDisplayMapFragmentToLoginDialogFragment()
        findNavController().navigate(action)
    }

    private fun goToAddSocialActionFragment(socialActionId: String) {
        val action =
            DisplayMapFragmentDirections.actionDisplayMapFragmentToSocialActionAddFragment(
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
                bottomSheetDialog.dismiss()
                goToAddSocialActionFragment(currentAction.id)
            }
            btDelete.setOnClickListener {
                bottomSheetDialog.dismiss()
                showDeleteConfirmationDialog(currentAction)
            }
        }
    }


    private fun getLocationFromAddress(strAddress: String): GeoPoint {
        val coder = Geocoder(requireContext())
        val address = coder.getFromLocationName(strAddress, 5)
        val location = address[0]
        return GeoPoint(
            location.latitude,
            location.longitude
        )
    }

    private fun centerCameraAnimation(myMap: GoogleMap, zoom: Float, duration: Int){
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())

        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    val googlePlex = CameraPosition.builder()
                        .target(LatLng(location!!.latitude, location.longitude))
                        .zoom(zoom)
                        .bearing(0f)
                        .tilt(45f)
                        .build()

                    myMap.animateCamera(
                        CameraUpdateFactory.newCameraPosition(googlePlex),
                        duration,
                        null
                    )
                }
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }
}