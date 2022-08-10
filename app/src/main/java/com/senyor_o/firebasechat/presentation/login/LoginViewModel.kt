package com.senyor_o.firebasechat.presentation.login

import android.content.Context
import android.util.Patterns
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.ActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInResult
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.senyor_o.firebasechat.R
import com.senyor_o.firebasechat.utils.*
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {

    val state: MutableState<LoginState> = mutableStateOf(LoginState())

    fun login(email: String, password: String, context: Context) {
        val errorMessage = if(email.isBlank() || password.isBlank()) {
            R.string.error_input_empty
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            R.string.error_not_a_valid_email
        } else null

        errorMessage?.let {
            state.value = state.value.copy(errorMessage = it)
            return
        }

        viewModelScope.launch {
            state.value = state.value.copy(displayProgressBar = true)

            logInWithMailAndPassword(
                email = email,
                password = password,
                context = context,
                onLoginSuccess = {
                    state.value = state.value.copy(email = it, successLogin = true, displayProgressBar = false)
                },
                onLoginFailure = {
                    state.value = state.value.copy(errorMessage = R.string.error_invalid_credentials, displayProgressBar = false)
                }
            )
        }
    }

    fun oneTapGoogleSignIn(
        launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
        oneTapClient: SignInClient,
        context: Context
    ) {
        val signInRequest = BeginSignInRequest.builder()
                .setGoogleIdTokenRequestOptions(
                    BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                        .setSupported(true)
                        // Your server's client ID, not your Android client ID.
                        .setServerClientId(context.getString(R.string.default_web_client_id))
                        // Only show accounts previously used to sign in.
                        .setFilterByAuthorizedAccounts(true)
                        .build()
                ).build()

        viewModelScope.launch {
            state.value = state.value.copy(displayGoogleProgressBar = true)
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener {
                    performAuthentication(
                        launcher,
                        it
                    )
                }.addOnFailureListener {
                    oneTapSignUp(
                        launcher,
                        oneTapClient,
                        context
                    )
                }
        }
    }

    private fun oneTapSignUp(
        launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
        oneTapClient: SignInClient,
        context: Context
    ) {

        val signUpRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(context.getString(R.string.default_web_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            ).build()

        oneTapClient.beginSignIn(signUpRequest)
            .addOnSuccessListener {
                performAuthentication(
                    launcher,
                    it
                )
                state.value = state.value.copy(firstTimeLogInWithGoogle = true)
            }.addOnFailureListener {
                state.value = state.value.copy(errorMessage = R.string.error_google, displayGoogleProgressBar = false)
            }
    }

    private fun performAuthentication(
        launcher: ManagedActivityResultLauncher<IntentSenderRequest, ActivityResult>,
        it: BeginSignInResult
    ) {
        val intent = IntentSenderRequest.Builder(it.pendingIntent.intentSender).build()
        launcher.launch(intent)
    }

    fun hideErrorDialog() {
        state.value = state.value.copy(
            errorMessage = null
        )
    }

    fun loginWithCredentials(googleIdToken: String?, context: Context) {
        viewModelScope.launch {
            loginWithGoogle(
                googleIdToken = googleIdToken,
                context = context,
                onLoginSuccess = {
                    state.value = state.value.copy(email = it.email!!, displayGoogleProgressBar = false, successLogin = true)
                    if(state.value.firstTimeLogInWithGoogle) {
                        addUserAdditionalData(
                            it,
                            hashMapOf(
                                DISPLAY_NAME to it.displayName,
                                PROFILE_PICTURE to it.photoUrl
                            )
                        )
                    }
                },
                onLoginFailure = {
                    state.value = state.value.copy(errorMessage = R.string.error_google, displayGoogleProgressBar = false)
                }
            )
        }
    }

}