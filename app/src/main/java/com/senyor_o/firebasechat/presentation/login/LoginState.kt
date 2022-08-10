package com.senyor_o.firebasechat.presentation.login

import androidx.annotation.StringRes

data class LoginState(
    val email: String = "",
    val password: String = "",
    val successLogin: Boolean = false,
    val displayProgressBar: Boolean = false,
    val displayGoogleProgressBar: Boolean = false,
    val firstTimeLogInWithGoogle: Boolean = false,
    @StringRes val errorMessage: Int? = null
)
