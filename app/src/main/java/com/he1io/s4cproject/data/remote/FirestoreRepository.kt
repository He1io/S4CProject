package com.he1io.s4cproject.data.remote

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.he1io.s4cproject.data.model.SocialAction


class FirestoreRepository {

    private val firestoreDB = FirebaseFirestore.getInstance()

    fun getSocialActionById(socialActionId: String): DocumentReference {
        return firestoreDB.collection("social_action").document(socialActionId)
    }

    fun getSavedSocialActions(): CollectionReference {
        return firestoreDB.collection("social_action")
    }

    fun saveSocialAction(socialAction: SocialAction): Task<Void> {
        val documentReference = firestoreDB.collection("social_action").document()
        socialAction.id = documentReference.id
        return documentReference.set(socialAction)
    }

    fun editSocialAction(socialAction: SocialAction): Task<Void> {
        return firestoreDB.collection("social_action").document(socialAction.id).set(socialAction)
    }

    fun deleteSocialAction(socialActionId: String): Task<Void> {
        return firestoreDB.collection("social_action").document(socialActionId).delete()
    }

}