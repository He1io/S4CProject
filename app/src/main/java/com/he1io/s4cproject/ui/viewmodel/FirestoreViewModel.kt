package com.he1io.s4cproject.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.EventListener
import com.he1io.s4cproject.data.remote.FirestoreRepository
import com.he1io.s4cproject.data.model.SocialAction


class FirestoreViewModel : ViewModel(){

    val TAG = "FIRESTORE_VIEW_MODEL"

    var firebaseRepository = FirestoreRepository()
    var savedSocialActions : MutableLiveData<List<SocialAction>> = MutableLiveData()
    var socialAction : MutableLiveData<SocialAction> = MutableLiveData()

    fun getSavedSocialActions(): LiveData<List<SocialAction>> {
        firebaseRepository.getSavedSocialActions().addSnapshotListener(EventListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                savedSocialActions.value = null
                return@EventListener
            }

            val savedSocialActionsList : MutableList<SocialAction> = mutableListOf()
            for (document in value!!) {
                //var addressItem = doc.toObject<SocialAction>()
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

        firebaseRepository.getSocialActionById(socialActionId).addSnapshotListener(EventListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                socialAction.value = null
                return@EventListener
            }

            value!!.data!!.let{
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

    fun saveSocialActionToFirebase(socialAction: SocialAction){
        firebaseRepository.saveSocialAction(socialAction).addOnFailureListener {
            Log.e(TAG,"Error al guardar la acción social!")
        }
    }

    fun editSocialAction(socialAction: SocialAction){
        firebaseRepository.editSocialAction(socialAction).addOnFailureListener {
            Log.e(TAG,"Error al editar la acción social!")
        }
    }

    fun deleteSocialAction(socialActionId: String){
        firebaseRepository.deleteSocialAction(socialActionId).addOnFailureListener {
            Log.e(TAG,"Error al borrar la acción social!")
        }
    }

}