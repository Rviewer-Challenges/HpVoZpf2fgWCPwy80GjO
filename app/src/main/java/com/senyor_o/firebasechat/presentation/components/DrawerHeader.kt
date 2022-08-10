package com.senyor_o.firebasechat.presentation.components

import android.net.Uri
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.IconButton
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import coil.request.ImageRequest
import com.senyor_o.firebasechat.R

@Composable
fun DrawerHeader(
    text: String,
    imageUrl: String?,
    onClick: () -> Unit
) {
    Column(
       modifier = Modifier
           .fillMaxWidth()
           .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconButton(
            modifier = Modifier
                .size(128.dp),
            onClick = {
                onClick()
            }
        ) {
            AvatarImage(
                imageUrl = imageUrl,
                modifier = Modifier
                    .size(128.dp)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.headlineMedium
        )
    }
}


data class MenuItem(
    val title: String,
    val contentDescription: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)