package com.senyor_o.firebasechat.presentation.login

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes
import com.senyor_o.firebasechat.R
import com.senyor_o.firebasechat.presentation.components.RoundedButton
import com.senyor_o.firebasechat.presentation.components.TransparentTextField
import com.senyor_o.firebasechat.presentation.components.EventDialog
import com.senyor_o.firebasechat.presentation.components.SocialMediaButton
import com.senyor_o.firebasechat.ui.theme.FirebaseChatTheme
import com.senyor_o.firebasechat.ui.theme.GMAILCOLOR

@ExperimentalMaterial3Api
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToRegister: () -> Unit,
) {
    val emailValue = rememberSaveable{ mutableStateOf("") }
    val passwordValue = rememberSaveable{ mutableStateOf("") }
    var passwordVisibility by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    val oneTapClient: SignInClient = remember {
        Identity.getSignInClient(context)
    }

    val oneTaplauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { result->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val credentials = oneTapClient.getSignInCredentialFromIntent(result.data)
                val googleIdToken = credentials.googleIdToken
                viewModel.loginWithCredentials(googleIdToken, context)
            } catch (apiException: ApiException) {
                when(apiException.statusCode) {
                    CommonStatusCodes.CANCELED -> {
                        Log.d("Auth", "One-tap dialog canceled")
                    }
                    CommonStatusCodes.NETWORK_ERROR -> {
                        viewModel.state.value = viewModel.state.value.copy(errorMessage = R.string.network_occurred)
                    }
                    else -> {
                        viewModel.state.value = viewModel.state.value.copy(errorMessage = R.string.error_google)
                    }
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ){
        Image(
            painter = painterResource(id = R.drawable.logo),
            contentDescription = "Login Image",
            contentScale = ContentScale.Inside
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ){
            ConstraintLayout {

                val surface = createRef()

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(550.dp)
                        .constrainAs(surface) {
                            bottom.linkTo(parent.bottom)
                        },
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(
                        topStartPercent = 8,
                        topEndPercent = 8
                    )
                ){
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceEvenly
                    ){
                        Text(
                            text = "Welcome Back!",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontWeight = FontWeight.Medium
                            )
                        )

                        Text(
                            text = "Login to your Account",
                            style = MaterialTheme.typography.headlineMedium.copy(
                                color = MaterialTheme.colorScheme.primary
                            )
                        )

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ){
                            TransparentTextField(
                                textFieldValue = emailValue,
                                textLabel = "Email",
                                keyboardType = KeyboardType.Email,
                                keyboardActions = KeyboardActions(
                                    onNext = {
                                        focusManager.moveFocus(FocusDirection.Down)
                                    }
                                ),
                                imeAction = ImeAction.Next
                            )

                            TransparentTextField(
                                textFieldValue = passwordValue,
                                textLabel = "Password",
                                keyboardType = KeyboardType.Password,
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        focusManager.clearFocus()

                                        viewModel.login(emailValue.value, passwordValue.value, context = context)
                                    }
                                ),
                                imeAction = ImeAction.Done,
                                trailingIcon = {
                                    IconButton(
                                        onClick = {
                                            passwordVisibility = !passwordVisibility
                                        }
                                    ) {
                                        Icon(
                                            imageVector = if(passwordVisibility) {
                                                Icons.Default.Visibility
                                            } else {
                                                Icons.Default.VisibilityOff
                                            },
                                            contentDescription = "Toggle Password Icon"
                                        )
                                    }
                                },
                                visualTransformation = if(passwordVisibility) {
                                    VisualTransformation.None
                                } else {
                                    PasswordVisualTransformation()
                                }
                            )

                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Forgot Password?",
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.End
                            )
                        }

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            RoundedButton(
                                text = "Login",
                                displayProgressBar = viewModel.state.value.displayProgressBar,
                                onClick = {
                                    viewModel.login(emailValue.value, passwordValue.value, context)
                                }
                            )
                        }

                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ){
                                Divider(
                                    modifier = Modifier.width(24.dp),
                                    thickness = 1.dp,
                                    color = Color.Gray
                                )

                                Text(
                                    modifier = Modifier.padding(8.dp),
                                    text = "OR",
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontWeight = FontWeight.Black
                                    )
                                )

                                Divider(
                                    modifier = Modifier.width(24.dp),
                                    thickness = 1.dp,
                                    color = Color.Gray
                                )
                            }

                            Text(
                                modifier = Modifier.fillMaxWidth(),
                                text = "Login with",
                                style = MaterialTheme.typography.bodyLarge.copy(
                                    MaterialTheme.colorScheme.primary
                                ),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ){
//                            SocialMediaButton(
//                                text = "Login with Facebook",
//                                onClick = {
//                                    facebookViewModel.login(context)
//                                },
//                                socialMediaColor = FACEBOOKCOLOR,
//                                displayProgressBar = viewModel.state.value.displayFacebookProgressBar
//                            )

                            SocialMediaButton(
                                text = "Login with Gmail",
                                onClick = {
                                    viewModel.oneTapGoogleSignIn(oneTaplauncher, oneTapClient, context)
                                },
                                socialMediaColor = GMAILCOLOR,
                                displayProgressBar = viewModel.state.value.displayGoogleProgressBar
                            )

                            ClickableText(
                                text = buildAnnotatedString {
                                    append("Do not have an Account? ")

                                    withStyle(
                                        style = SpanStyle(
                                            color = MaterialTheme.colorScheme.primary,
                                            fontWeight = FontWeight.Bold
                                        )
                                    ){
                                        append("Sign up")
                                    }
                                }
                            ){
                                onNavigateToRegister()
                            }
                        }
                    }
                }
            }
        }

        if(viewModel.state.value.errorMessage != null){
            EventDialog(
                errorMessage = viewModel.state.value.errorMessage!!,
                onDismiss = viewModel::hideErrorDialog
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    FirebaseChatTheme {
        LoginScreen(
            viewModel = hiltViewModel(),
            onNavigateToRegister = { }
        )
    }
}













