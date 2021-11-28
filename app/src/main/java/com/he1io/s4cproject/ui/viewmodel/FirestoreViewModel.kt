package com.he1io.s4cproject.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.he1io.s4cproject.data.remote.FirestoreRepository
import com.he1io.s4cproject.data.model.SocialAction


class FirestoreViewModel : ViewModel() {

    companion object {
        private const val TAG = "FIRESTORE_VIEW_MODEL"
    }

    private var firebaseRepository = FirestoreRepository()
    private var savedSocialActions: MutableLiveData<List<SocialAction>> = MutableLiveData()
    private var socialAction: MutableLiveData<SocialAction> = MutableLiveData()

    // Recuperar todas las acciones sociales como LiveData para que esté continuamente escuchando y se actualice
    fun getSavedSocialActions(): LiveData<List<SocialAction>> {
        firebaseRepository.getSavedSocialActions().addSnapshotListener(EventListener { value, e ->
            // Si hay alguna excepción
            if (e != null) {
                savedSocialActions.value = null
                return@EventListener
            }

            val savedSocialActionsList: MutableList<SocialAction> = mutableListOf()
            for (document in value!!) {
                /* "document.toObject<SocialAction>()" debería funcionar pero supongo que falla porque
                    el documento de firestore tiene el campo ID y en el constructor de SocialAction no lo tengo
                 */

                val socialAction = SocialAction(
                    document.data["name"].toString(),
                    document.data["year"].toString().toInt(),
                    document.data["mode"].toString(),
                    document.data["project"].toString(),
                    document.data["subvention"].toString().toDouble(),
                    document.data["spending"].toString().toDouble(),
                    document.data["country"].toString(),
                    document.data["region"].toString(),
                    document.data["administration"].toString()
                )
                socialAction.id = document.data["id"].toString()
                savedSocialActionsList.add(socialAction)
            }
            savedSocialActions.value = savedSocialActionsList
        })

        return savedSocialActions
    }

    fun getSocialActionById(socialActionId: String): LiveData<SocialAction> {
        firebaseRepository.getSocialActionById(socialActionId)
            .addSnapshotListener(EventListener { value, e ->
                if (e != null) {
                    socialAction.value = null
                    return@EventListener
                }

                value!!.data!!.let {
                    socialAction.value = SocialAction(
                        it["name"].toString(),
                        it["year"].toString().toInt(),
                        it["mode"].toString(),
                        it["project"].toString(),
                        it["subvention"].toString().toDouble(),
                        it["spending"].toString().toDouble(),
                        it["country"].toString(),
                        it["region"].toString(),
                        it["administration"].toString()
                    )
                    socialAction.value!!.id = it["id"].toString()
                }

            })

        return socialAction
    }

    fun saveSocialActionToFirebase(socialAction: SocialAction) {
        firebaseRepository.saveSocialAction(socialAction).addOnFailureListener {
            Log.e(TAG, "Error al guardar la acción social!")
        }
    }

    fun editSocialAction(socialAction: SocialAction) {
        firebaseRepository.editSocialAction(socialAction).addOnFailureListener {
            Log.e(TAG, "Error al editar la acción social!")
        }
    }

    fun deleteSocialAction(socialActionId: String) {
        firebaseRepository.deleteSocialAction(socialActionId).addOnFailureListener {
            Log.e(TAG, "Error al borrar la acción social!")
        }
    }

}