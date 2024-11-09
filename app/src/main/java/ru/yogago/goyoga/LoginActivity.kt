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
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
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
import ru.yogago.goyoga.data.AppConstants
import ru.yogago.goyoga.service.TokenProvider
import java.util.*

/**
 * Firebase Authentication using a Google ID Token.
 * email and password authentication
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var tvRedirectSignUp: TextView
    private lateinit var etEmail: EditText
    private lateinit var etPass: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignInGoogle: Button

    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

    private lateinit var auth: FirebaseAuth

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

        auth = Firebase.auth
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
        setContentView(R.layout.activity_login)

        tvRedirectSignUp = findViewById(R.id.tvRedirectSignUp)
        btnLogin = findViewById(R.id.btnLogin)
        btnSignInGoogle = findViewById(R.id.btnSignInGoogle)
        etEmail = findViewById(R.id.etEmailAddress)
        etPass = findViewById(R.id.etPassword)


        signInRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(getString(R.string.server_client_id)) // Your web client ID from Firebase
                    .setFilterByAuthorizedAccounts(false) // Show all accounts, not just authorized
                    .build()
            )
            .build()

        btnLogin.setOnClickListener {
            Toast.makeText(this, "Logging In", Toast.LENGTH_SHORT).show()
            login()
        }

        btnSignInGoogle.setOnClickListener { view: View? ->
            Toast.makeText(this, "Logging In", Toast.LENGTH_SHORT).show()
            oneTapSignIn()
        }

        tvRedirectSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }

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
                    TokenProvider.firebaseToken = task.result.token
                    // Send token to your backend via HTTPS
                    Log.d(AppConstants.LOG_TAG, "token: " + TokenProvider.firebaseToken)
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

    private fun login() {
        val email = etEmail.text.toString()
        val pass = etPass.text.toString()

        Log.d(AppConstants.LOG_TAG, "email: $email, pass: $pass" )
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                val user = FirebaseAuth.getInstance().currentUser
                if (user != null && user.isEmailVerified) {
                    Toast.makeText(this, "Successfully LoggedIn", Toast.LENGTH_SHORT).show()
                    updateUserData()
                } else {
                    Toast.makeText(this, "Email not confirmed", Toast.LENGTH_SHORT).show()
                }
            } else
                Toast.makeText(this, "Log In failed ", Toast.LENGTH_SHORT).show()
        }
    }
}