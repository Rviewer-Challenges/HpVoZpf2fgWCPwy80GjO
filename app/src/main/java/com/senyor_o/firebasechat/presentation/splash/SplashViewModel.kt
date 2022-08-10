package com.senyor_o.firebasechat.presentation.splash

import android.content.Context
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.senyor_o.firebasechat.R
import com.senyor_o.firebasechat.presentation.login.LoginState
import com.senyor_o.firebasechat.utils.EMAIL_METHOD
import com.senyor_o.firebasechat.utils.GOOGLE_METHOD
import com.senyor_o.firebasechat.utils.logInWithMailAndPassword
import com.senyor_o.firebasechat.utils.loginWithGoogle
import kotlinx.coroutines.launch

class SplashViewModel: ViewModel() {

    val state: MutableState<SplashState> = mutableStateOf(SplashState())

    fun validateSession(context: Context) {
        val prefs =
            context.getSharedPreferences(context.getString(R.string.prefs_file), Context.MODE_PRIVATE)
        viewModelScope.launch {
            val provider = prefs?.getString("provider", null)
            if (provider != null ) {
                if(provider == EMAIL_METHOD) {
                    val email = prefs.getString("email", null)
                    val password = prefs.getString("password", null)
                    if (!email.isNullOrEmpty() && !password.isNullOrEmpty())
                        logInWithMailAndPassword(
                            email,
                            password,
                            context,
                            onLoginSuccess = {
                                state.value = state.value.copy(sessionRetrieved = true, sessionSuccessful = true)
                            },
                            onLoginFailure = {
                                state.value = state.value.copy(sessionRetrieved = true)
                            }
                        )
                } else if (provider == GOOGLE_METHOD) {
                    val tokenId = prefs.getString("token", null)
                    viewModelScope.launch {
                        loginWithGoogle(
                            tokenId,
                            context,
                            onLoginSuccess = {
                                state.value = state.value.copy(sessionRetrieved = true, sessionSuccessful = true)
                            },
                            onLoginFailure = {
                                state.value = state.value.copy(sessionRetrieved = true)
                            }
                        )
                    }
                }
            } else {
                state.value = state.value.copy(sessionRetrieved = true)
            }
        }
    }
}