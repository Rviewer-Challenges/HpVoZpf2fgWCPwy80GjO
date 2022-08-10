package com.senyor_o.firebasechat.presentation.home


import android.icu.text.SimpleDateFormat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.firebase.auth.FirebaseAuth
import com.senyor_o.firebasechat.R
import com.senyor_o.firebasechat.presentation.components.DrawerHeader
import com.senyor_o.firebasechat.presentation.components.MenuItem
import com.senyor_o.firebasechat.presentation.components.MessageItem
import com.senyor_o.firebasechat.presentation.components.SendMessageBar
import com.senyor_o.firebasechat.ui.theme.FirebaseChatTheme
import com.senyor_o.firebasechat.utils.*
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
) {
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val messageValue = rememberSaveable{ mutableStateOf("") }
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    LaunchedEffect(viewModel.state.value.messages.size){
        listState.animateScrollToItem(
            if (viewModel.state.value.messages.size > 0) {
                viewModel.state.value.messages.size - 1
            } else {
                0
            }
        )
    }
    val addProfilePictureLauncher =  rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            viewModel.uploadPhoto(
                uri,
                name = FirebaseAuth.getInstance().currentUser?.uid!!,
                callback = viewModel::addProfilePicture
            )
        }
    }
    val sendPhotoLauncher =  rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            viewModel.uploadPhoto(
                uri,
                name ="${FirebaseAuth.getInstance().currentUser?.uid!!}-${System.currentTimeMillis()}",
                callback = {
                    viewModel.addMessage(
                        IMAGE_TYPE,
                        it,
                        onSuccess = {}
                    )
                }
            )
        }
    }
    val items = listOf(
        MenuItem(
            title = "Log Out",
            contentDescription = "Log Out button",
            icon = Icons.Default.Logout,
            onClick = {
                viewModel.logOut(context)
            }
        )
    )
    val selectedItem = remember { mutableStateOf(null) }
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerHeader(
                viewModel.state.value.name,
                viewModel.state.value.profileImage
            ) {
                addProfilePictureLauncher.launch("image/*")
            }
            items.forEach { item ->
                NavigationDrawerItem(
                    icon = { Icon(item.icon, contentDescription = item.contentDescription) },
                    label = { Text(item.title) },
                    selected = item == selectedItem.value,
                    onClick = {
                        item.onClick()
                        scope.launch { drawerState.close() }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                )
            }
        }
    ) {
        Scaffold(
            modifier = Modifier.background(MaterialTheme.colorScheme.background),
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(stringResource(id = R.string.app_name)) },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    if (drawerState.isClosed) {
                                        drawerState.open()
                                    } else {
                                        drawerState.close()
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Menu,
                                contentDescription = "Toggle Drawer"
                            )
                        }
                    }
                )
            },
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                SendMessageBar(
                    modifier = Modifier.padding(8.dp),
                    textFieldValue = messageValue,
                    textLabel = "Enter a message...",
                    keyboardType = KeyboardType.Text,
                    keyboardActions = KeyboardActions(
                        onDone = {
                            focusManager.clearFocus()
                        }
                    ),
                    trailingIcon = {
                        Row {
                            IconButton(
                                onClick = {
                                    sendPhotoLauncher.launch("image/*")
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AttachFile,
                                    contentDescription = "Send Message"
                                )
                            }
                            Spacer(modifier = Modifier
                                .width(
                                    8.dp
                                )
                            )
                            IconButton(
                                onClick = {
                                    if(!messageValue.value.isNullOrEmpty()) {
                                        viewModel.addMessage(
                                            TEXT_TYPE,
                                            messageValue.value,
                                            onSuccess = {
                                                messageValue.value = ""
                                            }
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Send,
                                    contentDescription = "Send Message"
                                )
                            }
                        }

                    },
                    imeAction = ImeAction.Done
                )
            },
            content = { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(innerPadding),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    state = listState
                ) {
                    viewModel.state.value.messages.forEach { message ->
                        item {
                            MessageItem(
                                type = message.contentType,
                                content = message.content.observeAsState().value,
                                isCurrentUser = message.isCurrentUser,
                                time = message.sentDate,
                                imageUrl = message.user.observeAsState().value?.ppf,
                                userName = message.user.observeAsState().value?.displayName
                            )
                        }
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    FirebaseChatTheme {
        HomeScreen(viewModel = hiltViewModel())
    }
}