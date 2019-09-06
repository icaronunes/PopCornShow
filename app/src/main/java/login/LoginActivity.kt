package login

import activity.BaseActivity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import br.com.icaro.filme.R
import com.crashlytics.android.Crashlytics
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.*
import main.MainActivity
import java.util.*

/**
 * Created by icaro on 06/11/16.
 */

class LoginActivity : BaseActivity(), GoogleApiClient.OnConnectionFailedListener {
    private var mAuthProgressDialog: Lazy<ProgressDialog> = lazy {
        createDialog()
    }
    private val TAG = this.javaClass.name
    private var mAuth: FirebaseAuth? = null
    private var stateListener: FirebaseAuth.AuthStateListener? = null
    private var mCallbackManager: CallbackManager? = null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null

    private val authStateListener: FirebaseAuth.AuthStateListener
        get() = FirebaseAuth.AuthStateListener {
            val user = FirebaseAuth.getInstance().currentUser
            if (user != null) {
                logUser()
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            } else {
                Log.d(TAG, "não logou... ")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(baseContext)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        mAuth = FirebaseAuth.getInstance()
        setContentView(R.layout.activity_login)

        stateListener = authStateListener

        hideSoftKeyboard()
        setFacebook()

        vincular_login.setOnClickListener {
            createDialogNewUser()
        }

        recuperar_senha.setOnClickListener {
            createDialgoResetPass()
        }
    }

    private fun createDialgoResetPass() {
        val dialog = AlertDialog.Builder(this@LoginActivity)
                .setView(R.layout.reset_password)
                .create()
        dialog.show()

        dialog.findViewById<Button>(R.id.bt_recuperar_cancel)?.setOnClickListener { dialog.dismiss() }

        dialog.findViewById<Button>(R.id.bt_recuperar_senha)?.setOnClickListener {
            val email = dialog.findViewById<TextInputLayout>(R.id.ed_email_recuperar)?.editText?.text.toString()
            if (email.contains("@") && email.contains(".")) {
                mAuth!!.sendPasswordResetEmail(email).addOnCompleteListener {
                    Toast.makeText(this@LoginActivity, resources.getString(R.string.email_recuperacao_enviado), Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
            } else {
                Toast.makeText(this@LoginActivity, resources.getString(R.string.email_invalido), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createDialogNewUser() {
        val dialog = AlertDialog.Builder(this@LoginActivity)
                .setView(R.layout.create_login)
                .create()
        dialog.show()

        val login = dialog.findViewById<TextInputLayout>(R.id.create_login)
        val senha = dialog.findViewById<TextInputLayout>(R.id.criar_pass)
        val repetirSenha = dialog.findViewById<TextInputLayout>(R.id.criar_repetir_pass)

        dialog.findViewById<MaterialButton>(R.id.bt_new_login_cancel)?.setOnClickListener { dialog.dismiss() }

        dialog.findViewById<MaterialButton>(R.id.bt_new_login_ok)?.setOnClickListener {
            if (validarParametros(login!!, senha!!, repetirSenha!!)) {
                criarLoginEmail(login.editText!!.text.toString(), senha.editText!!.text.toString())
                dialog.dismiss()
            } else {
                Toast.makeText(this@LoginActivity, R.string.ops,
                        Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createDialog(): ProgressDialog {
        return ProgressDialog(this).apply {
            setTitle("Loading")
            setMessage("Authenticating with PopCorn Show...")
            setCancelable(false)
        }
    }

    private fun validarParametros(login: TextInputLayout, senha: TextInputLayout, repetirSenha: TextInputLayout): Boolean {
        val loginReceive: String = login.editText!!.text.toString()
        val passReceive: String = senha.editText!!.text.toString()
        val checkPass: String = repetirSenha.editText!!.text.toString()

        if (loginReceive.contains("@") && loginReceive.contains(".")) {
            if (passReceive === checkPass && passReceive.length > 6) {
                return true
            }
        }

        return false
    }

    private fun setFacebook() {
        mCallbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(mCallbackManager!!, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                accessFacebook(loginResult.accessToken)
            }

            override fun onCancel() {
            }

            override fun onError(error: FacebookException) {
            }
        })

    }

    private fun accessFacebook(accessToken: AccessToken) {
        accessLoginData("facebook", accessToken.token)
    }

    fun onclick(view: View) {
        when (view.id) {
            R.id.logar -> {
                logarComEmail()
            }
            R.id.facebook -> {
                logarFacebook()
            }
            R.id.bt_anonimous -> {
                logarAnonimous()
            }
        }
    }

    private fun logarFacebook() {
        LoginManager
                .getInstance()
                .logInWithReadPermissions(
                        this,
                        Arrays.asList("public_profile", "email")
                )
    }

    private fun logarComEmail() {
        if (pass?.text.toString().length > 4 && login.text.toString().length > 4) {
            mAuthProgressDialog.value.show()
            mAuth?.signInWithEmailAndPassword(login.text.toString(), pass?.text.toString())
                    ?.addOnCompleteListener(this) { task ->
                        /*  Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        //   Log.d(TAG, "signInWithEmail:onComplete: " + email.getText().toString() + " " + pass.getText().toString());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                         signed in user can be handled in the listener. */
                        if (!task.isSuccessful) {
                            Toast.makeText(this@LoginActivity, R.string.ops,
                                    Toast.LENGTH_SHORT).show()
                        }
                        if (!isDestroyed)
                            mAuthProgressDialog.value.dismiss()
                    }?.addOnFailureListener {
                        Log.w(TAG, "signInWithEmail:failed " + it.message)
                        if (!isDestroyed)
                            mAuthProgressDialog.value.dismiss()
                    }
        } else {
            if (!isDestroyed) {
                Toast.makeText(this@LoginActivity, R.string.ops,
                        Toast.LENGTH_SHORT).show()
                mAuthProgressDialog.value.dismiss()
            }
        }
    }

    private fun logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        if (mAuth?.currentUser != null) {
            Crashlytics.setUserIdentifier(if (mAuth?.currentUser != null) mAuth?.currentUser!!.uid else "")
            Crashlytics.setUserEmail(mAuth?.currentUser?.email)
            Crashlytics.setUserName(mAuth?.currentUser?.displayName)
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            super.onActivityResult(requestCode, resultCode, data)
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            mCallbackManager?.onActivityResult(requestCode, resultCode, data)
            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "facebook")
            mFirebaseAnalytics?.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
        } catch (E: Exception) {
            Toast.makeText(this, R.string.ops, Toast.LENGTH_SHORT).show()
        }
    }

    private fun accessLoginData(provider: String, vararg tokens: String) {
        mAuthProgressDialog.value.show()
        if (tokens.isNotEmpty()) {

            var credential = FacebookAuthProvider.getCredential(tokens[0])
            credential = if (provider.equals("google", ignoreCase = true)) GoogleAuthProvider.getCredential(tokens[0], null) else credential
            mAuth!!.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Toast.makeText(this@LoginActivity, R.string.ops, Toast.LENGTH_SHORT).show()
                        }
                        if (!isDestroyed) {
                            mAuthProgressDialog.value.dismiss()
                        }
                    }
                    .addOnFailureListener { e ->
                        if (!isDestroyed) {
                            Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
                            mAuthProgressDialog.value.dismiss()
                        }
                    }
        } else {
            if (mAuth?.currentUser != null) {
                mAuth?.signOut()
            }
            if (!isDestroyed)
                mAuthProgressDialog.value.dismiss()
        }
    }


    private fun criarLoginEmail(email: String, pass: String) {
        mAuthProgressDialog.value.show()
        mAuth!!.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) { task ->

            /* If sign in fails, display a message to the user. If sign in succeeds
            // the auth state listener will be notified and logic to handle the
            // signed in user can be handled in the listener.*/

            val bundle = Bundle()
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "email")
            mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)

            if (task.isSuccessful) {
                Toast.makeText(this@LoginActivity, R.string.success, Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@LoginActivity, R.string.ops, Toast.LENGTH_SHORT).show()
            }
            if (!isDestroyed)
                mAuthProgressDialog.value.dismiss()
        }.addOnFailureListener { e ->
            if (!isDestroyed) {
                Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
                mAuthProgressDialog.value.dismiss()
            }
        }

    }


    override fun onStart() {
        super.onStart()
        mAuth?.addAuthStateListener(stateListener!!)
    }

    override fun onStop() {
        super.onStop()
        if (stateListener != null) {
            mAuth?.removeAuthStateListener(stateListener!!)
        }
    }

    override fun onConnectionFailed(connectionResult: ConnectionResult) {}

    fun logarAnonimous() {
        mAuthProgressDialog.value.show()
        mAuth!!.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    /*  Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());
                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.*/
                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "anonimo")
                    mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
                    Toast.makeText(this@LoginActivity, resources.getString(R.string.anonimo_alerta),
                            Toast.LENGTH_LONG).show()

                    if (!task.isSuccessful) {
                        Toast.makeText(this@LoginActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                    if (!isDestroyed)
                        mAuthProgressDialog.value.dismiss()
                }
    }
}
