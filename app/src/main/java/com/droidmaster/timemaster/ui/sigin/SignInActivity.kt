package com.droidmaster.timemaster.ui.sigin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.Constraints.TAG
import com.droidmaster.timemaster.MainActivity
import com.droidmaster.timemaster.R
import com.droidmaster.timemaster.databinding.ActivitySignInBinding
import com.droidmaster.timemaster.model.User
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private var user: User? = null
    private lateinit var auth: FirebaseAuth
    private lateinit var v: View


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater,null,false)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        binding.btnGoogleSign.setOnClickListener {
            // Check if user is signed in (non-null) and update UI accordingly.
            updateUI()
        }
    }

    private fun updateUI() {
        binding?.progress?.visibility = View.VISIBLE
        if (user == null) createSignInIntent() else nextPage()
    }

    private fun nextPage() {
        binding?.progress?.visibility = View.INVISIBLE
       /* val action =
            SignFragmentDirections.nextCategoryProfile(
                currentUser = user!!
            )
        Navigation.findNavController(v).navigate(action)*/
        startActivity(Intent(this,MainActivity::class.java))
    }

    private val signInLauncher = registerForActivityResult(
        FirebaseAuthUIActivityResultContract()
    ) { res ->
        this.onSignInResult(res)
    }


    private fun createSignInIntent() {
        // [START auth_fui_create_intent]
        // Choose authentication providers
        val providers = arrayListOf(
            AuthUI.IdpConfig.GoogleBuilder().build())

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance()
            .createSignInIntentBuilder()
            .setAvailableProviders(providers)
            .build()
        signInLauncher.launch(signInIntent)
        // [END auth_fui_create_intent]
    }

    // [START auth_fui_result]
    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult) {
        val response = result.idpResponse
        if (result.resultCode == RESULT_OK) {
            // Successfully signed in
            Log.d(TAG, "signInWithCredential:success")
            val user = FirebaseAuth.getInstance().currentUser
            saveUser(user)
            // ...
        } else {
            // Sign in failed. If response is null the user canceled the
            // sign-in flow using the back button. Otherwise check
            // response.getError().getErrorCode() and handle the error.
            // ...
           // Log.w(TAG, "signInWithCredential:failure", "deu ruim")
            Snackbar.make(v, "Salvo com sucesso", Snackbar.LENGTH_LONG).show()
        }
    }
    // [END auth_fui_result]




    private fun saveUser(user: FirebaseUser?) {
        this.user = User(
            uuid =  user?.uid,
            name = user?.displayName,
            phoneNumber = user?.phoneNumber,
            email = user?.email
        )
        FirebaseFirestore.getInstance()
            .collection("users")
            .add(this.user!!)
            .addOnSuccessListener {
                updateUI()
            }
            .addOnFailureListener {
                Snackbar.make(
                    v,
                    "Não foi possivel salvar, tente novamente mais tarde",
                    Snackbar.LENGTH_LONG
                ).show()
            }
    }




    companion object {
        internal const val RC_SIGN_IN = 1337
    }
}