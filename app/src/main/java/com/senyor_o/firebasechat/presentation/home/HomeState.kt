package com.senyor_o.firebasechat.presentation.home

import android.net.Uri
import androidx.annotation.StringRes
import com.senyor_o.firebasechat.presentation.home.model.Message

data class HomeState(
    val name: String = "",
    val profileImage: String? = null,
    val messages: MutableList<Message> = mutableListOf(),
    val successLogOut: Boolean = false,
    val displayProgressBar: Boolean = false,
    @StringRes val errorMessage: Int? = null,

    )
