package com.senyor_o.firebasechat.presentation.registration

import android.content.Context
import android.util.Patterns
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.senyor_o.firebasechat.R
import com.senyor_o.firebasechat.utils.DISPLAY_NAME
import com.senyor_o.firebasechat.utils.EMAIL_METHOD
import com.senyor_o.firebasechat.utils.PROFILE_PICTURE
import com.senyor_o.firebasechat.utils.addUserAdditionalData
import kotlinx.coroutines.launch

class RegisterViewModel: ViewModel() {

    val state: MutableState<RegisterState> = mutableStateOf(RegisterState())

    fun register(
        name: String,
        email: String,
        password: String,
        confirmPassword: String,
        context: Context
    ) {
        val errorMessage = if(name.isBlank() || email.isBlank() ||  password.isBlank() || confirmPassword.isBlank()){
            R.string.error_input_empty
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            R.string.error_not_a_valid_email
        } else if(password != confirmPassword) {
            R.string.error_incorrectly_repeated_password
        } else null

        errorMessage?.let {
            state.value = state.value.copy(errorMessage = errorMessage)
            return
        }

        viewModelScope.launch {
            state.value = state.value.copy(displayProgressBar = true)

            FirebaseAuth.getInstance().
                createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        state.value = state.value.copy(email = it.result.user?.email!!, successRegister = true)
                        val prefs = context.getSharedPreferences(context.getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
                        prefs.putString("provider", EMAIL_METHOD)
                        prefs.putString("email", it.result.user?.email!!)
                        prefs.putString("password", password)
                        prefs.apply()
                        addUserAdditionalData(
                            it.result.user!!,
                            hashMapOf(
                                DISPLAY_NAME to name,
                                PROFILE_PICTURE to ""
                            )
                        )
                    } else {
                        state.value = state.value.copy(errorMessage = R.string.error_occurred)
                    }
            }

            state.value = state.value.copy(displayProgressBar = false)
        }
    }

    fun hideErrorDialog() {
        state.value = state.value.copy(
            errorMessage = null
        )
    }

}