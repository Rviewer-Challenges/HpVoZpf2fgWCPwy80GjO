package com.senyor_o.firebasechat.utils

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import kotlinx.coroutines.tasks.await

fun addUserAdditionalData(
    user: FirebaseUser,
    data: HashMap<String, Any?>
) {
    Firebase.firestore.collection(USERS).document(user.uid).set(
        data
    )
}

suspend fun updateUserAdditionalData(
    user: FirebaseUser,
    data: HashMap<String, Any?>
) {
    Firebase.firestore
        .collection(USERS)
        .document(user.uid).update(
        data
    ).await()
}

suspend fun uploadPhoto(
    uri: Uri,
    name: String,
    mimeType: String?,
    callback: (url: String) -> Unit
) {

    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference

    val fileRef = storageRef.child("images/$name")
    val metadata = mimeType?.let {
        StorageMetadata.Builder()
            .setContentType(mimeType)
            .build()
    }
    if(metadata != null) {
        fileRef.putFile(uri, metadata).await()
    } else {
        fileRef.putFile(uri)
    }
    callback(fileRef.downloadUrl.await().toString())
}
