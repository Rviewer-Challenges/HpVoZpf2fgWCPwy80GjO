package com.senyor_o.firebasechat.presentation.home.model

import androidx.lifecycle.MutableLiveData

data class Message(
    val contentType: String,
    val content: MutableLiveData<String>,
    val user: MutableLiveData<User>,
    val sentDate: String,
    val isCurrentUser: Boolean,
)
