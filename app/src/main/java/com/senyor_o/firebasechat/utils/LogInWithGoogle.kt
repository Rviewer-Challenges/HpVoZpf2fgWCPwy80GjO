package com.senyor_o.firebasechat.utils

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.senyor_o.firebasechat.R

fun loginWithGoogle(
    googleIdToken: String?,
    context: Context,
    onLoginSuccess: (FirebaseUser) -> Unit = {},
    onLoginFailure: () -> Unit = {}
) {
    val credential = GoogleAuthProvider.getCredential(googleIdToken, null)
    FirebaseAuth.getInstance()
        .signInWithCredential(credential)
        .addOnCompleteListener {
            if (it.isSuccessful) {
                val prefs = context.getSharedPreferences(context.getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                prefs.putString("provider", GOOGLE_METHOD)
                prefs.putString("token", googleIdToken)
                prefs.apply()
                onLoginSuccess(it.result.user!!)
            } else {
                onLoginFailure()
            }
        }.addOnFailureListener {
            onLoginFailure()
        }
}