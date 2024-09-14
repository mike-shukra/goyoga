package ru.yogago.goyoga

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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

/**
 * Demonstrate Firebase Authentication using a Google ID Token.
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var tvRedirectSignUp: TextView
    private lateinit var etEmail: EditText
    private lateinit var etPass: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSignInGoogle: Button

    private val Req_Code: Int = 123

    private lateinit var auth: FirebaseAuth

    private lateinit var googleSignInClient: GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_login)

        // View Binding
        tvRedirectSignUp = findViewById(R.id.tvRedirectSignUp)
        btnLogin = findViewById(R.id.btnLogin)
        btnSignInGoogle = findViewById(R.id.btnSignInGoogle)
        etEmail = findViewById(R.id.etEmailAddress)
        etPass = findViewById(R.id.etPassword)


        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        auth = Firebase.auth

        btnLogin.setOnClickListener {
            Toast.makeText(this, "Logging In", Toast.LENGTH_SHORT).show()
            login()
        }

        btnSignInGoogle.setOnClickListener { view: View? ->
            Toast.makeText(this, "Logging In", Toast.LENGTH_SHORT).show()
            signIn()
        }

        tvRedirectSignUp.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onStart() {
        super.onStart()

        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            updateUI(auth.currentUser)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(AppConstants.LOG_TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(AppConstants.LOG_TAG, "Google sign in failed", e)
            }
        }
    }
    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(AppConstants.LOG_TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    Log.d(AppConstants.LOG_TAG, "user: " + user!!.email)
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(AppConstants.LOG_TAG, "signInWithCredential:failure", task.exception)
                    updateUI(null)
                }
            }
    }
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    private fun updateUI(user: FirebaseUser?) {
        Log.d(AppConstants.LOG_TAG, "auth.currentUser!!: " + auth.currentUser!!)
        auth.currentUser!!.getIdToken(true)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    TokenProvider.firebaseToken = task.result.token
                    // Send token to your backend via HTTPS
                    Log.d(AppConstants.LOG_TAG, "token: " + TokenProvider.firebaseToken)
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
//                     Handle error -> task.getException();
                }
            }
//        val credential = GoogleAuthProvider.getCredential(user!!.uid, null)
//        auth.signInWithCredential(credential).addOnCompleteListener { task ->
//            if (task.isSuccessful) {
////                SavedPreference.setEmail(this, account.email.toString())
////                SavedPreference.setUsername(this, account.displayName.toString())
//                val intent = Intent(this, MainActivity::class.java)
//                startActivity(intent)
//                finish()
//            }
//        }
    }

    private fun login() {
        val email = etEmail.text.toString()
        val pass = etPass.text.toString()
        // calling signInWithEmailAndPassword(email, pass)
        // function using Firebase auth object
        // On successful response Display a Toast
        Log.d(AppConstants.LOG_TAG, "email: $email, pass: $pass" )
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
            Log.d(AppConstants.LOG_TAG, "try " )
            if (it.isSuccessful) {
                Toast.makeText(this, "Successfully LoggedIn", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                // using finish() to end the activity
                finish()
                // ...

            } else
                Toast.makeText(this, "Log In failed ", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 123
    }
}