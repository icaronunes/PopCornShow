package activity


import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import br.com.icaro.filme.R
import com.crashlytics.android.Crashlytics
import com.facebook.*
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
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


class LoginActivity : AppCompatActivity(), GoogleApiClient.OnConnectionFailedListener {
    private val TAG = this.javaClass.name
    private var mAuth: FirebaseAuth? = null
    private var stateListener: FirebaseAuth.AuthStateListener? = null
    private var mAuthProgressDialog: ProgressDialog? = null
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
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        FirebaseApp.initializeApp(baseContext)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        mAuth = FirebaseAuth.getInstance()
        FacebookSdk.sdkInitialize(baseContext)
        setContentView(R.layout.activity_login)
        val recuperar = findViewById<View>(R.id.recuperar_senha) as TextView

        stateListener = authStateListener

        hideSoftKeyboard()
        setFacebook()

        mAuthProgressDialog = ProgressDialog(this)
        mAuthProgressDialog?.setTitle("Loading")
        mAuthProgressDialog?.setMessage("Authenticating with PopCorn Show...")
        mAuthProgressDialog?.setCancelable(false)

        val criarLogin = findViewById<View>(R.id.vincular_login) as TextView
        criarLogin.setOnClickListener {
            val dialog = AlertDialog.Builder(this@LoginActivity)
                    .setView(R.layout.criar_login)
                    .create()
            dialog.show()

            val cancel = dialog.findViewById<View>(R.id.bt_new_login_cancel) as Button?
            val login = dialog.findViewById<View>(R.id.vincular_login) as TextInputLayout?
            val senha = dialog.findViewById<View>(R.id.criar_pass) as TextInputLayout?
            val repetirSenha = dialog.findViewById<View>(R.id.criar_repetir_pass) as TextInputLayout?

            cancel!!.setOnClickListener { dialog.dismiss() }

            val ok = dialog.findViewById<View>(R.id.bt_new_login_ok) as Button?
            ok!!.setOnClickListener {
                if (validarParametros(login!!, senha!!, repetirSenha!!)) {
                    criarLoginEmail(login.editText!!.text.toString(), senha.editText!!.text.toString())
                    dialog.dismiss()
                }
            }
        }

        recuperar.setOnClickListener {
            val dialog = AlertDialog.Builder(this@LoginActivity)
                    .setView(R.layout.recuperar_senha_layout)
                    .create()
            dialog.show()

            val ok = dialog.findViewById<View>(R.id.bt_recuperar_senha) as Button?
            val cancel = dialog.findViewById<View>(R.id.bt_recuperar_cancel) as Button?


            cancel!!.setOnClickListener { dialog.dismiss() }

            ok!!.setOnClickListener {
                val editText = dialog.findViewById<View>(R.id.ed_email_recuperar) as TextInputLayout?
                val email = editText!!.editText!!.text.toString()
                // Log.d(TAG, email);
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

    }

    private fun hideSoftKeyboard() {
        window.setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        )
    }


    private fun validarParametros(login: TextInputLayout, senha: TextInputLayout, repetirSenha: TextInputLayout): Boolean {
        val login: String = login.editText!!.text.toString()
        val senha: String = senha.editText!!.text.toString()
        val repetir: String = repetirSenha.editText!!.text.toString()

        if (login.contains("@") && login.contains(".")) {
            if (senha == repetir && senha.length > 6) {
                return true
            }
        }

        return false
    }

    private fun setFacebook() {
        mCallbackManager = CallbackManager.Factory.create()

        LoginManager.getInstance().registerCallback(mCallbackManager!!, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                //  Log.d(TAG, "facebook:onSuccess: " + loginResult.getAccessToken());
                accessFacebook(loginResult.accessToken)
            }

            override fun onCancel() {
                // Log.d(TAG, "facebook:onCancel ");
            }

            override fun onError(error: FacebookException) {
                // Log.d(TAG, "facebook:onError ", error);
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
            mAuth?.signInWithEmailAndPassword(login.text.toString(), pass?.text.toString())
                    ?.addOnCompleteListener(this) { task ->
                        //  Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                        //   Log.d(TAG, "signInWithEmail:onComplete: " + email.getText().toString() + " " + pass.getText().toString());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful) {
                            Toast.makeText(this@LoginActivity, R.string.ops,
                                    Toast.LENGTH_SHORT).show()
                        }
                    }?.addOnFailureListener {
                          Log.w(TAG, "signInWithEmail:failed " + it.message)
                    }
        } else {
            Toast.makeText(this@LoginActivity, R.string.ops,
                    Toast.LENGTH_SHORT).show()
        }
    }

    //Crash
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
            //  Log.d(TAG, "Google Result");
            if (requestCode == RC_SIGN_IN) {
                val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
                if (result.isSuccess) {

                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "google")
                    mFirebaseAnalytics?.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)

                    // Google Sign In was successful, authenticate with Firebase
                    val account = result.signInAccount
                    //Removido acesso ao Google
                    accessGoogle(account!!.idToken)
                } else {
                     Log.d(TAG, "Falha no login Google");
                }
            } else {
                mCallbackManager?.onActivityResult(requestCode, resultCode, data)
                val bundle = Bundle()
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "facebook")
                mFirebaseAnalytics?.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
            }
        } catch (E: Exception) {
            Toast.makeText(this, R.string.ops, Toast.LENGTH_SHORT).show()
        }
    }

     private fun accessGoogle(token: String?) {
        accessLoginData("google", token!!)
    }

    private fun accessLoginData(provider: String, vararg tokens: String) {
        mAuthProgressDialog!!.show()
        if (tokens != null
                && tokens.size > 0
                && tokens[0] != null) {

            var credential = FacebookAuthProvider.getCredential(tokens[0])

            credential = if (provider.equals("google", ignoreCase = true)) GoogleAuthProvider.getCredential(tokens[0], null) else credential
            mAuth!!.signInWithCredential(credential)
                    .addOnCompleteListener { task ->
                        if (!task.isSuccessful) {
                            Toast.makeText(this@LoginActivity, R.string.ops, Toast.LENGTH_SHORT).show()
                        }

                        mAuthProgressDialog!!.dismiss()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show()
                        mAuthProgressDialog!!.dismiss()
                    }
        } else {
            if (mAuth!!.currentUser != null) {
                mAuth!!.signOut()
            }
        }

    }


    private fun criarLoginEmail(email: String, pass: String) {
        mAuthProgressDialog?.show()
        mAuth!!.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this) { task ->
                    //   Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.

                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "email")
                    mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)

                    if (task.isSuccessful) {
                        Toast.makeText(this@LoginActivity, R.string.success, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@LoginActivity, R.string.ops, Toast.LENGTH_SHORT).show()
                    }
                }.addOnFailureListener { e -> Toast.makeText(this@LoginActivity, e.message, Toast.LENGTH_SHORT).show() }
        mAuthProgressDialog?.hide()
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
        mAuth!!.signInAnonymously()
                .addOnCompleteListener(this) { task ->
                    //  Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());
                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    val bundle = Bundle()
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "anonimo")
                    mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.LOGIN, bundle)
                    Toast.makeText(this@LoginActivity, resources.getString(R.string.anonimo_alerta),
                            Toast.LENGTH_LONG).show()
                    if (!task.isSuccessful) {
                        Toast.makeText(this@LoginActivity, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                    }
                }
    }

    companion object {
        private const val RC_SIGN_IN = 1
    }
}
