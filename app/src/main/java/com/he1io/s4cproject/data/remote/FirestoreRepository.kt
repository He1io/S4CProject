package com.he1io.s4cproject.data.remote

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.he1io.s4cproject.data.model.SocialAction


class FirestoreRepository {

    val TAG = "FIREBASE_REPOSITORY"
    var firestoreDB = FirebaseFirestore.getInstance()
    var user = FirebaseAuth.getInstance().currentUser


    // save address to firebase
    fun saveSocialAction(socialAction: SocialAction): Task<Void> {
        //var
        var documentReference = firestoreDB.collection("social_action").document()
        socialAction.id = documentReference.id
        return documentReference.set(socialAction)
    }

    // get saved addresses from firebase
    fun getSavedSocialActions(): CollectionReference {
        var collectionReference = firestoreDB.collection("social_action")
        return collectionReference
    }
/*
    fun deleteAddress(addressItem: AddressItem): Task<Void> {
        var documentReference =  firestoreDB.collection("users/${user!!.email.toString()}/saved_addresses")
            .document(addressItem.addressId)

        return documentReference.delete()
    }
*/
}