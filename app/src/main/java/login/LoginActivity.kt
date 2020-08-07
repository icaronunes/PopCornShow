package login

import activity.BaseActivity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
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
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_login.login
import kotlinx.android.synthetic.main.activity_login.pass
import kotlinx.android.synthetic.main.activity_login.recuperar_senha
import kotlinx.android.synthetic.main.activity_login.vincular_login
import main.MainActivity
import utils.kotterknife.findView
import utils.makeToast
import java.util.Arrays

/**
 * Created by icaro on 06/11/16.
 */
class LoginActivity : BaseActivity() {
	private val TAG = this.javaClass.name
	private var mAuth: FirebaseAuth? = null
	private var mCallbackManager: CallbackManager? = null
	private val model: LoginViewModel by lazy { createViewModel(LoginViewModel::class.java, this) }
	private val send: EditText by findView(R.id.pass)
	private val authStateListener: FirebaseAuth.AuthStateListener
		get() = FirebaseAuth.AuthStateListener {
			val user = it.currentUser
			if (user != null) {
				startActivity(Intent(this@LoginActivity, MainActivity::class.java))
				finish()
			} else {
				Log.d(TAG, "não logou... ")
			}
		}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		FirebaseApp.initializeApp(baseContext)
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

		send.setOnEditorActionListener { _, actionId, _ ->
			when (actionId) {
				EditorInfo.IME_ACTION_SEND -> {
					hideSoftKeyboard()
					logarComEmail()
					true
				}
				else -> false
			}
		}
	}

	private fun createDialgoResetPass() {
		val dialog = AlertDialog.Builder(this@LoginActivity)
			.setView(R.layout.reset_password)
			.create()
		dialog.show()

		dialog.findViewById<Button>(R.id.bt_recuperar_cancel)
			?.setOnClickListener { dialog.dismiss() }

		dialog.findViewById<Button>(R.id.bt_recuperar_senha)?.setOnClickListener {
			val email =
				dialog.findViewById<TextInputLayout>(R.id.ed_email_recuperar)?.editText?.text.toString()
			if (android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
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

		dialog.findViewById<MaterialButton>(R.id.bt_new_login_cancel)
			?.setOnClickListener { dialog.dismiss() }

		dialog.findViewById<MaterialButton>(R.id.bt_new_login_ok)?.setOnClickListener {
			if (model.validarParametros(login!!, senha!!, repetirSenha!!)) {
				criarLoginEmail(login.editText!!.text.toString(), senha.editText!!.text.toString())
				dialog.dismiss()
			} else {
				makeToast(R.string.ops)
			}
		}
	}

	private fun setFacebook() {
		mCallbackManager = CallbackManager.Factory.create()

		LoginManager.getInstance()
			.registerCallback(mCallbackManager!!, object : FacebookCallback<LoginResult> {
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
				}?.addOnFailureListener {
					makeToast(R.string.ops)
					Log.w(TAG, "signInWithEmail:failed " + it.message)
				}
		} else {
			makeToast(R.string.ops)
		}
	}

	public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		try {
			super.onActivityResult(requestCode, resultCode, data)
			// Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
			mCallbackManager?.onActivityResult(requestCode, resultCode, data)
		} catch (E: Exception) {
			makeToast(R.string.ops)
		}
	}

	private fun accessLoginData(provider: String, vararg tokens: String) {
		if (tokens.isNotEmpty()) {
			var credential = FacebookAuthProvider.getCredential(tokens[0])
			credential =
				if (provider.equals("google", ignoreCase = true)) GoogleAuthProvider.getCredential(
					tokens[0],
					null
				) else credential
			mAuth!!.signInWithCredential(credential)
				.addOnCompleteListener { task ->
					if (!task.isSuccessful) {
						makeToast(R.string.ops)
					}
				}
				.addOnFailureListener { e ->
					makeToast(e.message)
				}
		} else {
			if (mAuth?.currentUser != null) {
				mAuth?.signOut()
			}
		}
	}

	private fun criarLoginEmail(email: String, pass: String) {
		mAuth?.createUserWithEmailAndPassword(email, pass)?.addOnCompleteListener(this) { task ->
			/* If sign in fails, display a message to the user. If sign in succeeds
			// the auth state listener will be notified and logic to handle the
			// signed in user can be handled in the listener.*/
			if (task.isSuccessful) {
				makeToast(R.string.success)
			} else {
				makeToast(R.string.ops)
			}
		}?.addOnFailureListener { e ->
			makeToast(e.message)
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

	override fun onDestroy() {
		if (currentFocus != null) {
			val imm: InputMethodManager =
				getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
			imm.hideSoftInputFromWindow(currentFocus?.windowToken, 0)
		}
		super.onDestroy()
	}

	fun logarAnonimous() {
		mAuth?.signInAnonymously()
			?.addOnCompleteListener(this) { task ->
				/*  Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());
				// If sign in fails, display a message to the user. If sign in succeeds
				// the auth state listener will be notified and logic to handle the
				// signed in user can be handled in the listener.*/

				makeToast(R.string.anonimo_alerta, Toast.LENGTH_LONG)
				if (!task.isSuccessful) {
					this.makeToast("Authentication failed.")
				}
			}
	}
}
