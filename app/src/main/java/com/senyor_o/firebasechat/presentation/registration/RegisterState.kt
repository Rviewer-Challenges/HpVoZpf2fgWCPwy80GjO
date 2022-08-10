package com.senyor_o.firebasechat.presentation.registration

import androidx.annotation.StringRes

data class RegisterState(
    val email: String = "",
    val successRegister: Boolean = false,
    val displayProgressBar: Boolean = false,
    @StringRes val errorMessage: Int? = null
)
