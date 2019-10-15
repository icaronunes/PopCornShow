package login

import activity.BaseActivity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import br.com.icaro.filme.R
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import java.util.Arrays
import kotlinx.android.synthetic.main.activity_login.login
import kotlinx.android.synthetic.main.activity_login.pass
import kotlinx.android.synthetic.main.activity_login.recuperar_senha
import kotlinx.android.synthetic.main.activity_login.vincular_login
import main.MainActivity
import utils.makeToast

/**
 * Created by icaro on 06/11/16.
 */

class LoginActivity : BaseActivity() {
    private var mAuthProgressDialog: Lazy<ProgressDialog> = lazy {
        createDialog()
    }
    private val TAG = this.javaClass.name
    private var mAuth: FirebaseAuth? = null
    private var mCallbackManager: CallbackManager? = null
    private var mFirebaseAnalytics: FirebaseAnalytics? = null
    private lateinit var model: LoginViewModel

    private val authStateListener: FirebaseAuth.AuthStateListener
        get() = FirebaseAuth.AuthStateListener {
            val user = it.currentUser
            if (user != null) {
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finishAffinity()
            } else {
                Log.d(TAG, "não logou... ")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(baseContext)
        model = createViewModel(LoginViewModel::class.java)
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this)
        mAuth = FirebaseAuth.getInstance()
        setContentView(R.layout.activity_login)
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
                    makeToast(R.string.email_recuperacao_enviado)
                    dialog.dismiss()
                }
            } else {
                makeToast(R.string.email_invalido)
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
            if (model.validarParametros(login!!, senha!!, repetirSenha!!)) {
                criarLoginEmail(login.editText!!.text.toString(), senha.editText!!.text.toString())
                dialog.dismiss()
            } else {
                makeToast(R.string.ops)
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
                            makeToast(R.string.ops)
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
                makeToast(R.string.ops)
                mAuthProgressDialog.value.dismiss()
            }
        }
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        try {
            super.onActivityResult(requestCode, resultCode, data)
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            mCallbackManager?.onActivityResult(requestCode, resultCode, data)
            mFirebaseAnalytics?.logEvent(FirebaseAnalytics.Event.LOGIN, Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_NAME, "facebook")
            })
        } catch (E: Exception) {
            makeToast(R.string.ops)
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
                            makeToast(R.string.ops)
                        }
                        if (!isDestroyed) {
                            mAuthProgressDialog.value.dismiss()
                        }
                    }
                    .addOnFailureListener { e ->
                        if (!isDestroyed) {
                            makeToast(e.message)
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
        mAuth?.createUserWithEmailAndPassword(email, pass)?.addOnCompleteListener(this) { task ->

            /* If sign in fails, display a message to the user. If sign in succeeds
            // the auth state listener will be notified and logic to handle the
            // signed in user can be handled in the listener.*/
            mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.LOGIN, Bundle().apply {
                putString(FirebaseAnalytics.Param.ITEM_NAME, email)
            })

            if (task.isSuccessful) {
                makeToast(R.string.success)
            } else {
                makeToast(R.string.ops)
            }
            if (!isDestroyed)
                mAuthProgressDialog.value.dismiss()
        }?.addOnFailureListener { e ->
            if (!isDestroyed) {
                makeToast(e.message)
                mAuthProgressDialog.value.dismiss()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        mAuth?.addAuthStateListener(authStateListener)
    }

    override fun onStop() {
        super.onStop()
        mAuth?.removeAuthStateListener(authStateListener)
    }

    fun logarAnonimous() {
        mAuthProgressDialog.value.show()
        mAuth?.signInAnonymously()
                ?.addOnCompleteListener(this) { task ->
                    /*  Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());
                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.*/
                    mFirebaseAnalytics!!.logEvent(FirebaseAnalytics.Event.LOGIN, Bundle().apply {
                        putString(FirebaseAnalytics.Param.ITEM_NAME, "anonimo") })

                    this@LoginActivity.makeToast(R.string.anonimo_alerta)
                    if (!task.isSuccessful) {
                        this.makeToast("Authentication failed.")
                    }
                    if (!isDestroyed)
                        mAuthProgressDialog.value.dismiss()
                }
    }
}
