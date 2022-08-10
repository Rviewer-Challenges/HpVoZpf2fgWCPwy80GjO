package com.senyor_o.firebasechat.presentation.home


import android.content.Context
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.senyor_o.firebasechat.R
import com.senyor_o.firebasechat.presentation.home.model.Message
import com.senyor_o.firebasechat.presentation.home.model.User
import com.senyor_o.firebasechat.utils.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.*

class HomeViewModel: ViewModel() {

    val state: MutableState<HomeState> = mutableStateOf(HomeState())

    init {
        getMessages()
        getName()
        getProfilePicture()
    }

    fun logOut(context: Context) {
        val prefs = context.getSharedPreferences(context.getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.clear()
        prefs.apply()
        FirebaseAuth.getInstance().signOut()
        state.value = state.value.copy(successLogOut = true)
    }

    fun addMessage(
        type: String,
        message: String,
        onSuccess: () -> Unit
    ) {
        if (FirebaseAuth.getInstance().currentUser?.uid != null) {
            Firebase.firestore.collection(MESSAGES).document().set(
                hashMapOf(
                    CONTENT_TYPE to type,
                    CONTENT to message,
                    SENT_BY to Firebase.auth.currentUser?.uid,
                    SENT_ON to System.currentTimeMillis(),
                    SENT_ON_IMAGE to state.value.profileImage

                )
            ).addOnSuccessListener {
                onSuccess()
            }
        }
    }

    private fun getMessages() {
        if (FirebaseAuth.getInstance().currentUser?.uid != null) {
            Firebase.firestore.collection(MESSAGES)
                .orderBy(SENT_ON)
                .addSnapshotListener { value, e ->
                    if (e!= null) {
                        return@addSnapshotListener
                    }
                    val list = emptyList<Message>().toMutableList()

                    if (value!= null) {
                        for(doc in value) {
                            val data = doc.data
                            data[IS_CURRENT_USER] = Firebase.auth.currentUser?.uid.toString() == data[SENT_BY].toString()
                            list.add(Message(
                                contentType = data[CONTENT_TYPE].toString(),
                                content = MutableLiveData<String>().apply{
                                    postValue(data[CONTENT].toString())
                                },
                                user = getUser(data[SENT_BY].toString()),
                                isCurrentUser = Firebase.auth.currentUser?.uid.toString() == data[SENT_BY].toString(),
                                sentDate = getFormattedDate(data[SENT_ON].toString().toLong())
                            ))
                        }
                    }

                    state.value = state.value.copy(messages = list)
                }
        }
    }

    private fun getFormattedDate(timeInMillis: Long) : String {
        // define once somewhere in order to reuse it
        val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

// JVM representation of a millisecond epoch absolute instant
        val instant = Instant.ofEpochMilli(timeInMillis)

// Adding the timezone information to be able to format it (change accordingly)
        val date = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
        return formatter.format(date) // 10/12/2019 06:35:45
    }

    private fun getUser(userUid: String): MutableLiveData<User> {
        val result = MutableLiveData<User>()
        viewModelScope.launch {
            Firebase.firestore.collection(USERS)
                .document(userUid)
                .get().addOnSuccessListener {
                    result.postValue(
                        User(it[DISPLAY_NAME].toString(), it[PROFILE_PICTURE].toString())
                    )
                }
        }
        return result;
    }

    private fun getName() {
        viewModelScope.launch {
            if (FirebaseAuth.getInstance().currentUser?.uid != null) {
                Firebase.firestore.collection(USERS)
                    .document(FirebaseAuth.getInstance().currentUser?.uid!!)
                    .get().addOnSuccessListener {
                        state.value = state.value.copy(name = it[DISPLAY_NAME].toString())
                    }.await()
            }
        }
    }

    private fun getProfilePicture() {
        viewModelScope.launch {
            if (FirebaseAuth.getInstance().currentUser?.uid != null) {
                Firebase.firestore.collection(USERS)
                    .document(FirebaseAuth.getInstance().currentUser?.uid!!)
                    .addSnapshotListener { value, e ->
                        if (e!= null) {
                            return@addSnapshotListener
                        }

                        if (value!= null) {
                            val data = value.data!!
                            state.value = state.value.copy(profileImage = data[PROFILE_PICTURE].toString())
                        }
                    }

            }
        }
    }

    fun addProfilePicture(imageUrl: String) {
        viewModelScope.launch {
            updateUserAdditionalData(
                FirebaseAuth.getInstance().currentUser!!,
                hashMapOf(
                    PROFILE_PICTURE to imageUrl
                )
            )
        }
    }

    fun uploadPhoto(
        imageUri: Uri,
        callback: (String) -> Unit,
        name: String
    ) {
        viewModelScope.launch {
            uploadPhoto(
                imageUri,
                name,
                "image/png",
                callback = {
                    callback(it)
                }
            )
        }
    }
}