package com.senyor_o.firebasechat.utils

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.senyor_o.firebasechat.R

fun logInWithMailAndPassword(
    email: String,
    password: String,
    context: Context,
    onLoginSuccess: (String) -> Unit = {},
    onLoginFailure: () -> Unit = {}
) {
    FirebaseAuth
        .getInstance()
        .signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                val prefs = context.getSharedPreferences(context.getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                prefs.putString("provider", EMAIL_METHOD)
                prefs.putString("email", email)
                prefs.putString("password", password)
                prefs.apply()
                onLoginSuccess(it.result.user?.email!!)
            } else {
                onLoginFailure()
            }
        }
}