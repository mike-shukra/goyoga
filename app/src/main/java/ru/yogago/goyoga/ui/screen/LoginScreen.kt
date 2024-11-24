package ru.yogago.goyoga.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ru.yogago.goyoga.ui.components.EmailTextField
import ru.yogago.goyoga.ui.components.GoogleSignInButton
import ru.yogago.goyoga.ui.components.LoginButton
import ru.yogago.goyoga.ui.components.PasswordTextField
import ru.yogago.goyoga.ui.components.SignUpRedirectText

@Composable
fun LoginScreen(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    isLoading: Boolean, // Принимаем состояние загрузки
    onLoginClick: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    onRedirectSignUpClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF303030)) // colorPrimary
            .padding(horizontal = 24.dp, vertical = 32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmailTextField(
            email = email,
            onEmailChange = onEmailChange
        )
        PasswordTextField(
            password = password,
            onPasswordChange = onPasswordChange
        )
        val isLoginEnabled = email.isNotEmpty() && password.isNotEmpty()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            LoginButton(
                isLoginEnabled = isLoginEnabled,
                isLoading = isLoading,
                onLoginClick = onLoginClick
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            GoogleSignInButton(
                onClick = onGoogleSignInClick
            )
        }
        SignUpRedirectText(onClick = onRedirectSignUpClick)
    }
}

@Composable
fun LoginScreenWithState(
    onLoginClick: (String, String, (Boolean) -> Unit) -> Unit, // Callback для управления состоянием
    onGoogleSignInClick: () -> Unit,
    onRedirectSignUpClick: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    LoginScreen(
        email = email,
        onEmailChange = { email = it },
        password = password,
        onPasswordChange = { password = it },
        isLoading = isLoading,
        onLoginClick = {
            onLoginClick(email, password) { loading ->
                isLoading = loading
            }
        },
        onGoogleSignInClick = onGoogleSignInClick,
        onRedirectSignUpClick = onRedirectSignUpClick
    )
}