package login

import android.app.Application
import applicaton.BaseViewModel
import com.google.android.material.textfield.TextInputLayout

class LoginViewModel(app: Application) : BaseViewModel(app) {

    fun validarParametros(login: TextInputLayout, senha: TextInputLayout, repetirSenha: TextInputLayout): Boolean {
        val loginReceive: String = login.editText!!.text.toString()
        val passReceive: String = senha.editText!!.text.toString()
        val checkPass: String = repetirSenha.editText!!.text.toString()

        if (android.util.Patterns.EMAIL_ADDRESS.matcher(loginReceive).matches()) {
            if (passReceive === checkPass && passReceive.length > 6) {
                return true
            }
        }

        return false
    }
}
