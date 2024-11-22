package ru.yogago.goyoga

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import com.google.firebase.Firebase
import dagger.hilt.android.AndroidEntryPoint
import ru.yogago.goyoga.data.AppConstants
import java.util.*
import javax.inject.Inject

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstrainScope
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

/**
 * Firebase Authentication using a Google ID Token.
 * email and password authentication
 */

@AndroidEntryPoint
class LoginActivity : ComponentActivity() {

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    @Inject
    lateinit var auth: FirebaseAuth

    companion object {
        var dLocale: Locale = Locale("")
    }

    init {
        if(dLocale != Locale("") ) {
            Locale.setDefault(dLocale)
            val configuration = Configuration()
            configuration.setLocale(dLocale)
            this.applyOverrideConfiguration(configuration)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                LoginScreenWithState(
                    onLoginClick = { email, password, onLoadingChange ->
                        login(email, password, onLoadingChange)
                    },
                    onGoogleSignInClick = {
                        Toast.makeText(this, "Logging In with Google", Toast.LENGTH_SHORT).show()
                        oneTapSignIn()
                    },
                    onRedirectSignUpClick = {
                        val intent = Intent(this, SignUpActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                )
            }
        }
        oneTapClient = Identity.getSignInClient(this)

        val currentUser = auth.currentUser
        if (currentUser != null && currentUser.isEmailVerified) {
            Toast.makeText(this, "Successfully LoggedIn", Toast.LENGTH_SHORT).show()
            updateUserData()
        } else {
            if (currentUser != null) {
                if (!currentUser.isEmailVerified)
                    Toast.makeText(this, "Email not confirmed", Toast.LENGTH_LONG).show()
            }
        }

        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.server_client_id)) // Your web client ID from Firebase
                    .setFilterByAuthorizedAccounts(false) // Show all accounts, not just authorized
                    .build()
            )
            .build()

    }

    private fun oneTapSignIn() {
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener { result ->
                startForResult.launch(IntentSenderRequest.Builder(result.pendingIntent.intentSender).build())
            }
            .addOnFailureListener { e ->
                // Handle failure, for example, no Google accounts available
                e.printStackTrace()
            }
    }


    // Register the sign-in result handler
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            try {
                // Retrieve the Google ID token from the sign-in result
                val credential: SignInCredential = oneTapClient.getSignInCredentialFromIntent(result.data)
                val idToken = credential.googleIdToken
                if (idToken != null) {
                    // Use Google ID token to authenticate with Firebase
                    firebaseAuthWithGoogle(idToken)
                }
            } catch (e: ApiException) {
                e.printStackTrace()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign-in succeeded, get the Firebase user
                    val user = auth.currentUser
                    // You can now access user data like user?.email or user?.uid
                    Log.d(AppConstants.LOG_TAG, "Firebase Auth successful, user: ${user?.email}")
                    updateUserData()
                } else {
                    // Sign-in failed
                    Log.d(AppConstants.LOG_TAG, "Firebase Auth failed: ${task.exception}")
                    Toast.makeText(this, "Log In failed ", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun updateUserData() {
        auth.currentUser!!.getIdToken(true)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Send token to your backend via HTTPS
                    Log.d(AppConstants.LOG_TAG, "token: " + task.result.token)
                    navigateToHomeScreen()
                } else {
                    task.exception
                    Log.d(AppConstants.LOG_TAG, "exception: " + task.exception)
                }
            }
    }
    private fun navigateToHomeScreen() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun login(email: String, pass: String, onLoadingChange: (Boolean) -> Unit) {
        Log.d(AppConstants.LOG_TAG, "email: $email, pass: $pass")
        lifecycleScope.launch {
            try {
                onLoadingChange(true) // Показать индикатор загрузки
                val result = auth.signInWithEmailAndPassword(email, pass).await()
                val user = result.user
                if (user != null && user.isEmailVerified) {
                    Toast.makeText(this@LoginActivity, "Successfully LoggedIn", Toast.LENGTH_SHORT).show()
                    updateUserData()
                } else {
                    Toast.makeText(this@LoginActivity, "Email not confirmed", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Log In failed: ${e.message}", Toast.LENGTH_SHORT).show()
                Log.e(AppConstants.LOG_TAG, "Login error: ${e.message}")
            } finally {
                onLoadingChange(false) // Скрыть индикатор загрузки
            }
        }
    }

}

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
        // Email TextField
        val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        TextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = { Text("email", color = Color(0xFF757575)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 30.dp, start = 8.dp, end = 8.dp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = if (isEmailValid) Color(0xFF757575) else Color.Red,
                unfocusedIndicatorColor = if (isEmailValid) Color(0xFF757575) else Color.Red,
                textColor = Color.White
            ),
            isError = !isEmailValid,
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email)
        )

        // Password TextField
        TextField(
            value = password,
            onValueChange = onPasswordChange,
            placeholder = { Text("password", color = Color(0xFF757575)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 15.dp, start = 8.dp, end = 8.dp),
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = Color.Transparent,
                focusedIndicatorColor = Color(0xFF757575),
                unfocusedIndicatorColor = Color(0xFF757575),
                textColor = Color.White
            ),
            singleLine = true,
            visualTransformation = PasswordVisualTransformation()
        )

        // Login Button
        val isLoginEnabled = email.isNotEmpty() && password.isNotEmpty()
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = {
                    onLoginClick()
                },
                modifier = Modifier
                    .safeContentPadding()
                    .padding(bottom = 28.dp, top = 40.dp)
                    .height(58.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = if (isLoginEnabled) Color(0xFFDC8628) else Color(0xFFB7B7B7),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(32.dp),
                enabled = isLoginEnabled && !isLoading // Блокируем, если идет загрузка
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text(
                        text = stringResource(R.string.login).uppercase(),
                        fontSize = 22.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }

        // Google Sign-In Button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Button(
                onClick = {
                    onGoogleSignInClick() // Явный вызов, чтобы унифицировать
                },
                modifier = Modifier
                    .safeContentPadding()
                    .padding(bottom = 20.dp)
                    .height(58.dp),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFDC8628), // colorAccentDark
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(32.dp) // радиус
            ) {
                Text(
                    text = stringResource(R.string.google_sign_in).uppercase(),
                    fontSize = 22.sp,
                    modifier = Modifier.padding(horizontal = 8.dp))
            }
        }
        // Sign-Up Redirect Text
        Text(
            text = stringResource(R.string.don_t_have_an_account_sign_in),
            color = Color(0xFFFAFAFA),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .clickable(
                    onClick = onRedirectSignUpClick,
                    indication = LocalIndication.current, // Обратная связь Ripple
                    interactionSource = remember { MutableInteractionSource() }
                ),
            textAlign = TextAlign.End,
            fontSize = 18.sp
        )
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
        isLoading = isLoading, // Передаем текущее состояние
        onLoginClick = {
            onLoginClick(email, password) { loading ->
                isLoading = loading // Управляем состоянием загрузки
            }
        },
        onGoogleSignInClick = onGoogleSignInClick,
        onRedirectSignUpClick = onRedirectSignUpClick
    )
}

//@Preview(showBackground = true, backgroundColor = 0xFF303030)
//@Composable
//fun LoginScreenPreview() {
//    LoginScreen(
//        email = "",
//        onEmailChange = {},
//        password = "",
//        onPasswordChange = {},
//        onLoginClick = {},
//        onGoogleSignInClick = {},
//        onRedirectSignUpClick = {}
//    )
//}

