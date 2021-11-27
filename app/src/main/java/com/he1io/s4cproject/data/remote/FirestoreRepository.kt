package com.he1io.s4cproject.data.remote

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.he1io.s4cproject.data.model.SocialAction


class FirestoreRepository {

    val TAG = "FIREBASE_REPOSITORY"
    val firestoreDB = FirebaseFirestore.getInstance()

    fun getSocialActionById(socialActionId: String): DocumentReference {
        return firestoreDB.collection("social_action").document(socialActionId)
    }

    fun getSavedSocialActions(): CollectionReference {
        return firestoreDB.collection("social_action")
    }

    fun saveSocialAction(socialAction: SocialAction): Task<Void> {
        //var
        var documentReference = firestoreDB.collection("social_action").document()
        socialAction.id = documentReference.id
        return documentReference.set(socialAction)
    }

    fun editSocialAction(socialAction: SocialAction): Task<Void> {
        return firestoreDB.collection("social_action").document(socialAction.id).set(socialAction)
    }
/*
    fun deleteAddress(addressItem: AddressItem): Task<Void> {
        var documentReference =  firestoreDB.collection("users/${user!!.email.toString()}/saved_addresses")
            .document(addressItem.addressId)

        return documentReference.delete()
    }
*/
}