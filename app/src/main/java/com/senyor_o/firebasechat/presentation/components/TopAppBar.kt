package com.senyor_o.firebasechat.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.senyor_o.firebasechat.R

@ExperimentalMaterial3Api
@Composable
fun TopAppBar(
    onNavigationIconClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = { Text(stringResource(id = R.string.app_name)) },
        navigationIcon = {
            IconButton(
                onClick = {
                    onNavigationIconClick()
                }
            ) {
                Icon(
                    imageVector = Icons.Filled.Menu,
                    contentDescription = "Toggle Drawer"
                )
            }
        }
    )
}