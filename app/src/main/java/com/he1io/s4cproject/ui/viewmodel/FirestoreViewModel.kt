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

    fun saveSocialActionToFirebase(socialAction: SocialAction){
        firebaseRepository.saveSocialAction(socialAction).addOnFailureListener {
            Log.e(TAG,"Failed to save Address!")
        }
    }

    fun getSavedSocialActions(): LiveData<List<SocialAction>> {
        firebaseRepository.getSavedSocialActions().addSnapshotListener(EventListener { value, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                savedSocialActions.value = null
                return@EventListener
            }

            var savedSocialActionsList : MutableList<SocialAction> = mutableListOf()
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
                savedSocialActionsList.add(socialAction)
            }
            savedSocialActions.value = savedSocialActionsList
        })

        return savedSocialActions
    }
/*
    // delete an address from firebase
    fun deleteAddress(addressItem: AddressItem){
        firebaseRepository.deleteAddress(addressItem).addOnFailureListener {
            Log.e(TAG,"Failed to delete Address")
        }
    }
*/
}