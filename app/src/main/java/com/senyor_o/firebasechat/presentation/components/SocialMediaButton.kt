package com.senyor_o.firebasechat.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.senyor_o.firebasechat.ui.theme.GMAILCOLOR

@Composable
fun SocialMediaButton(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit,
    displayProgressBar: Boolean,
    socialMediaColor: Color
) {
    if(!displayProgressBar) {
        OutlinedButton(
            modifier = Modifier
                .width(280.dp)
                .height(50.dp),
            onClick = onClick,
            shape = RoundedCornerShape(50),
            colors = ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = socialMediaColor
            ),
            border = BorderStroke(
                width = (1.5).dp,
                color = socialMediaColor
            )
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = socialMediaColor
                )
            )
        }
    } else {
        CircularProgressIndicator(
            modifier = Modifier.size(50.dp),
            color = socialMediaColor,
            strokeWidth = 6.dp
        )
    }

}

@Preview
@Composable
fun SocialMediaButtonPreview() {
    SocialMediaButton(
        text = "Login with Gmail",
        onClick = { },
        socialMediaColor = GMAILCOLOR,
        displayProgressBar = false
    )
}



