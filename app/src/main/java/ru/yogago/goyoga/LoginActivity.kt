package ru.yogago.goyoga

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.identity.SignInCredential
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.AndroidEntryPoint
import ru.yogago.goyoga.data.AppConstants
import java.util.*
import javax.inject.Inject
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import ru.yogago.goyoga.ui.screen.LoginScreenWithState
import ru.yogago.goyoga.ui.theme.MyAppTheme

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
            MyAppTheme {
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

